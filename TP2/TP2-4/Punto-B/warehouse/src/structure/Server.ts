import express, { Express, Request, Response } from 'express';
import bodyParser from 'body-parser';

import RedisServer, { RedisCredencials } from './RedisServer';
import RabbitListener from './RabbitListener';

import dotenv from 'dotenv';
import { Message } from 'amqplib/callback_api';
dotenv.config();

const { joinImages } = require('join-images');

export type Process = {
	id: string,
	name: string,
	mimetype: string
	messagesId: string[],
	times: any
}

export type WrappedMessage = {
	processId: string,
	messageId: string,
	payload: any
}

const Log = (...args: any[]) => {
	console.log(`[WAREHOUSE-SERVER]`, ...args);
}

const LogError = (...args: any[]) => {
	Log(`- [ERROR]`, ...args);
}

export default class Server {
	private _express!: Express;
	private _redisServer!:RedisServer;
	private _rabbitListener!: RabbitListener;

	async initialize() {
		try {
			// Express
			this._express = express();
			this._express.use(bodyParser.json());
			// Redis
			await this.initializeRedis();
			// RabbitMQ
			await this.initializeRabbitMQ();
			// Endpoints
			this.initializeEndpoints();

			const port = process.env.PORT || 5001;
			this._express.listen(port, () => {
				Log(`Warehouse server running on port ${port}`);
			})
		} catch (error) {
			LogError('Something went wrong!!', error);
			process.exit(1);
		}
	}

	private async initializeRabbitMQ() {
		const config = {
			protocol: process.env.RABBIT_PROTOCOL,
			hostname: process.env.RABBIT_HOST,
			port: Number(process.env.RABBIT_PORT),
			username: process.env.RABBIT_USERNAME,
			password: process.env.RABBIT_PASSWORD
		};

		this._rabbitListener = new RabbitListener(config);
		await this._rabbitListener.initialize();
		this._rabbitListener.onIncomingMessage(this.handleIncomingMessage.bind(this));
		Log('Sucessfully connected to RabbitMQ service');
	}

	private async initializeRedis() {
		const config: RedisCredencials = {
			username: process.env.REDIS_USERNAME!,
			password: process.env.REDIS_PASSWORD!,
			port: Number(process.env.REDIS_PORT),
			host: process.env.REDIS_HOST!
		}

		this._redisServer = new RedisServer(config);
		await this._redisServer.initialize();
		Log('Sucessfully connected to Redis service');
	}

	private initializeEndpoints() {
		this._express.get('/', this.handleHomeRequest.bind(this));
		this._express.get('/process/:id', this.handleGetProcess.bind(this));
		this._express.post('/process', this.handlePostProcess.bind(this));
	}

	private handleHomeRequest(req: Request, res: Response) {
		return res.json({ message: "Sobel Images Warehouse Server is online" });
	}

	private async handleGetProcess(req: Request, res: Response) {
		try {
			const { id } = req.params;
			const process = await this._redisServer.getProcess(id);
			if (!process) return res.status(404).json({ message: 'Process not found!' });

			const receivedMessages = await this._redisServer.getReceivedMessages(process.id);

			if (process.messagesId.length !== receivedMessages.length) return res.json({ status: 'PENDING' });

			// Checks if the process full image is already cached
			const fullImage = await this._redisServer.getProcessFullImage(process.id);
			if (fullImage) return res.contentType(process.mimetype).send(fullImage);

			// Otherwise, fetch the process received messages and merge the image
			const mergedImgSharp = await this.joinImagesAsync(receivedMessages.map(message => Buffer.from(message.payload, 'base64')));
			const bufferImg: Buffer = await mergedImgSharp.png().toBuffer();

			// Stores the processed image
			await this._redisServer.setProcessFullImage(process.id, bufferImg);

			return res.contentType(process.mimetype).send(bufferImg);
		} catch (error) {
			LogError('Failed to get image', error);
			return res.status(500).json({ message: 'Ups!! Something went wrong!' });
		}
	}

	private async handlePostProcess(req: Request, res: Response) {
		try {
			const { process } = req.body;
			if (!process) return res.status(400).json('body.process is required!');

			const { id, messages, name, mimetype, time } = process;

			if (!id || typeof id !== 'string') return res.status(400).json({ message: 'body.process.id has to be a string!' });
			if (!name || typeof name !== 'string') return res.json({ message: 'body.process.name has to be a string' });
			if (!messages
				|| !Array.isArray(messages)
				|| messages.length < 1
				|| messages.some(messageId => typeof messageId !== 'string')) return res.status(400).json({ message: 'body.process.messages has to be an string[]' });

			const processToInsert: Process = {
				id: id,
				name,
				mimetype,
				messagesId: messages,
				times: time
			}

			await this._redisServer.setProcess(id, processToInsert);
			return res.json({ message: 'Process successfully received' });
		} catch (error) {
			LogError('Failed to get image', error);
			return res.status(500).json({ message: 'Ups!! Something went wrong!' });
		}
	}

	private joinImagesAsync(images: Buffer[]): any {
		return new Promise((res, rej) => {
			joinImages(images)
				.then((img: any) => {
					res(img);
				})
				.catch((error: Error) => {
					return rej(error);
				});
		})
	}

	private async handleIncomingMessage(rawMessage: Message | null) {
		if (!rawMessage) return;

		const message: WrappedMessage = JSON.parse(rawMessage.content.toString());
		Log(`Message received (ID ${message.messageId})`);

		// Gets the incoming message's process
		const process = await this._redisServer.getProcess(message.processId);
		if (!process) return;

		// Fetches the process's received messages
		const receivedMessages = await this._redisServer.getReceivedMessages(process.id);

		// Checks if the message has not being received yet
		const alreadyReceived = Boolean(receivedMessages.find(receivedMessage => receivedMessage.messageId === message.messageId));
		if (alreadyReceived) return;

		// Saves the received message
		await this._redisServer.addReceivedMessage(process.id, message);

		// Checks if the process has received all the messages
		const isProcessComplete = await this.isProcessComplete(process.id);
		if (isProcessComplete) await this.handleCompleteProcess(process.id);
	}

	private async isProcessComplete(processId: string) {
		const process = await this._redisServer.getProcess(processId);
		if (!process) throw Error('Process not found!');

		const receivedMessages = await this._redisServer.getReceivedMessages(processId);

		if (process.messagesId.length !== receivedMessages.length) return false;
		// Checks that every message has been received
		return process.messagesId.every(messageId => {
			return Boolean(receivedMessages.find(receiveMessage => messageId === receiveMessage.messageId));
		});
	}

	private async handleCompleteProcess(processId: string) {
		const process = await this._redisServer.getProcess(processId);
		if (!process) throw Error('Process not found!');

		const receivedMessages = await this._redisServer.getReceivedMessages(processId);

		const sortStartTime = new Date();

		// Sort received messages
		receivedMessages.sort((a, b) => {
			const indexOfA = process.messagesId.indexOf(a.messageId);
			const indexOfB = process.messagesId.indexOf(b.messageId);
			if (indexOfA > indexOfB) return 1;
			if (indexOfA < indexOfB) return -1;
			return 0;
		});

		const sortEndTime = new Date();

		// Merge the messages
		const mergeStartTime = new Date();
		const mergedImgSharp = await this.joinImagesAsync(receivedMessages.map(message => Buffer.from(message.payload, 'base64')));
		const bufferImg: Buffer = await mergedImgSharp.png().toBuffer();
		const mergeEndTime = new Date();

		const finishTime = new Date();

		// Calculates the times
		const startTime = new Date(process.times.startTime);
		const fragmentationStartTime = new Date(process.times.fragmentationTime.start);
		const fragmentationEndTime = new Date(process.times.fragmentationTime.end);

		const fragmentationTime = Number(fragmentationEndTime) - Number(fragmentationStartTime);
		const sortTotalTime = Number(sortEndTime) - Number(sortStartTime);
		const mergeTotalTime = Number(mergeEndTime) - Number(mergeStartTime);
		const totalTime = Number(finishTime) - Number(startTime);
		const workersTime = Number(totalTime) - Number(mergeTotalTime) - Number(sortTotalTime) - Number(fragmentationTime);

		// Stores the full image
		await this._redisServer.setProcessFullImage(process.id, bufferImg);
		
		this.logPerformance(process.id, totalTime, fragmentationTime, workersTime, sortTotalTime, mergeTotalTime);
		// Log(`Process Completed! (ID ${processId})`);
	}

	logPerformance(processId: string, totalTime: number, fragmentationTime: number, workersTime: number, sortTime: number, mergeTime: number) {
		Log(`
PROCESS COMPLETE! (ID ${processId})
Details:
	- Image Fragmentation Time...................${fragmentationTime}ms - ${(fragmentationTime * 100 / totalTime).toFixed(2)}%\n
	- Image Sobel Filter Application.............${workersTime}ms - ${(workersTime * 100 / totalTime).toFixed(2)}%\n
	- Image Fragments Sort.......................${sortTime}ms - ${(sortTime * 100 / totalTime).toFixed(2)}%\n
	- Image Fragments Merge......................${mergeTime}ms - ${(mergeTime * 100 / totalTime).toFixed(2)}%\n

	- TOTAL TIME: ${totalTime}ms
		`)
	}
}