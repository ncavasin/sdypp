import express, { Express, Request, Response } from 'express';
import bodyParser from 'body-parser';
import fileUpload from 'express-fileupload';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

const { imageToChunks } = require('split-images');
const { joinImages } = require('join-images');

import RabbitServer, { Process, WrappedMessage } from './RabbitServer';

import dotenv from 'dotenv';
dotenv.config();

const Log = (...args: any[]) => {
	console.log(`[SERVER]`, ...args);
}

const LogError = (...args: any[]) => {
	Log(`- [ERROR]`, ...args);
}


export default class Server {
	private _express!: Express;
	private _rabbitServer!: RabbitServer;

	async initialize() {
		try {
			// Express
			this._express = express();
			this._express.use(bodyParser.json());
			this._express.use(fileUpload());
			// RabbitMQ
			await this.initializeRabbitMQ();
			// Endpoints
			this.initializeEndpoints();

			const port = process.env.PORT || 4001;
			this._express.listen(port, () => {
				Log(`Decentralized server running on port ${port}`);
			})
		} catch (error) {
			LogError('Something went wrong!', error);
			process.exit(1);
		}
	}

	private async initializeRabbitMQ() {
		const config = {
			protocol: process.env.RABBIT_PROTOCOL,
			host: process.env.RABBIT_HOST,
			port: Number(process.env.RABBIT_PORT),
			username: process.env.RABBIT_USERNAME,
			password: process.env.RABBIT_PASSWORD
		};

		this._rabbitServer = new RabbitServer(config);
		await this._rabbitServer.initialize();
		this._rabbitServer.joinQueue(process.env.RABBIT_REQUEST_QUEUE!);
	}

	initializeEndpoints() {
		this._express.get('/', this.handleHomeRequest.bind(this));
		this._express.post('/sobel', this.handleSobelImageRequest.bind(this));
		this._express.get('/sobel/:processId', this.handleGetSobelImageRequest.bind(this));
	}

	private handleHomeRequest(req: Request, res: Response) {
		return res.json({ message: "Decentralized Sobel Server is online" });
	}

	private async handleGetSobelImageRequest(req: Request, res: Response) {
		try {
			const { processId } = req.params;
			if (!processId) return res.status(400).json('params.processId is required!');

			const response = await axios.get(`${process.env.WAREHOUSE_HOST}:${process.env.WAREHOUSE_PORT}/process/${processId}`, {
				responseType: 'arraybuffer'
			});

			res.setHeader('Content-Type', response.headers['content-type']);
			return res.send(response.data);
		} catch (error) {
			LogError('Failed to get image', error);
			return res.status(500).json({ message: 'Ups!! Something went wrong!' });
		}
	}

	private async handleSobelImageRequest(req: Request, res: Response) {
		try {
			const files = req.files;
			if (!files || Object.keys(files).length === 0 || !files.image) return res.status(400).json({ message: 'No image given' });

			const { image } = files as any;
			const { name, mimetype } = image;

			// Max height size of image chunks
			const chunckHeightSize = 1000;

			const chuncks: Buffer[] = await imageToChunks(image.data, chunckHeightSize);

			Log(`Process started! - File: "${image.name}" - Messages: ${chuncks.length}`);

			const createdProcess = this.createProcess(chuncks);

			await axios.post(`${process.env.WAREHOUSE_HOST}:${process.env.WAREHOUSE_PORT}/process/`, {
				process: {
					id: createdProcess.id,
					messages: createdProcess.messages.map(message => message.messageId),
					name,
					mimetype
				}
			})

			// Send the messages to the workers
			this._rabbitServer.QueueMessages(process.env.RABBIT_REQUEST_QUEUE!, createdProcess.messages);

			return res.json({ message: 'The image successfully upload', processId: createdProcess.id });
		} catch (error) {
			LogError(error);
			return res.status(500).json({ message: 'Ups!! Something went wrong!' });
		}
	}

	createProcess(messages: Buffer[]): Process {
		const processId = uuidv4();

		const parsedMessages: WrappedMessage[] = messages.map((message, index) => ({
			processId,
			messageId: `${index}`,
			payload: message.toString('base64')
		}));

		const processData: Process = {
			id: processId,
			messages: parsedMessages,
			receivedMessages: []
		}

		return processData;
	}

	joinImagesAsync(images: Buffer[]): any {
		return new Promise((res, rej) => {
			joinImages(images)
				.then((img: any) => {
					res(img);
				})
				.catch((error: Error) => {
					return rej(error);
				})
			
		})
	}
}