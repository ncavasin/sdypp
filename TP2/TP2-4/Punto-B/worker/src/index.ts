import amqp, { Connection, Channel } from 'amqplib/callback_api';
import fs from 'fs';
import { v4 as uuidV4 } from 'uuid';

const pixels = require('image-pixels');
const { Sobel } = require('sobel');
const imageOutput = require('image-output');

import dotenv from 'dotenv';
dotenv.config();

const config = {
	protocol: process.env.RABBIT_PROTOCOL,
	host: process.env.RABBIT_HOST,
	port: Number(process.env.RABBIT_PORT),
	username: process.env.RABBIT_USERNAME,
	password: process.env.RABBIT_PASSWORD
};


const requestQueue = process.env.RABBIT_REQUEST_QUEUE!;
const responseQueue = process.env.RABBIT_RESPONSE_QUEUE!

main();

async function main () {
	try {
		const connection = await connectToQueue(config);
		const channel = await createChannel(connection);
		joinOrCreateQueue(channel, requestQueue);

		console.log("[WORKER] Waiting for messages...");

		channel.consume(requestQueue, async function(rawMessage: any) {
			const message = JSON.parse(rawMessage.content.toString());

			console.log(`[WORKER] Processing received message... (Message ID ${message.messageId})`);

			const requestPayloadBuffer = Buffer.from(message.payload, 'base64');

			const sobelImageBuffer = await processSobelFilter(requestPayloadBuffer);

			const responsePayloadBuffer = sobelImageBuffer.toString('base64');
			message.payload = responsePayloadBuffer;

			channel.sendToQueue(responseQueue, Buffer.from(JSON.stringify(message)));
			console.log(`[WORKER] Processing completed! (Message ID ${message.messageId})`);
		}, {
			noAck: false
		});
	} catch (error) {
		console.error('Something went wrong!!', error);
	}
}

async function processSobelFilter(image: Buffer) {
	// Transform the image buffer into an Array of Pixels
	const { data, width, height } = await pixels(image);

	// Apply the sobel filter over the Array of Pixels
	const sobel = Sobel({ data, width, height });
	const sobelImageData = await sobel.toImageData();

	const filename = `${__dirname}/sobel-${uuidV4()}.jpg`;

	// Saves the Array of Pixeles into a image file
	await imageOutput(sobelImageData, filename);
	// Reads the file to get the binary code
	const sobelImageBuffer = fs.readFileSync(filename);
	// Deletes the persisted file
	fs.unlinkSync(filename);

	return sobelImageBuffer;
}

function asyncTimeout() {
	return new Promise((res, rej) => {
		setTimeout(() => {
			return res(null);
		}, 2000)
	})
}


function connectToQueue(config: any): Promise<Connection> {
	return new Promise((res, rej) => {
		amqp.connect(config, function (error, connection) {
			if (error) return rej(error);
			return res(connection);
		});
	})
}

function createChannel(connection: Connection): Promise<Channel> {
	return new Promise((res, rej) => {
		connection.createChannel(function (error, channel) {
			if (error) return rej(error);
			return res(channel);
		});
	})
}

function joinOrCreateQueue(channel: Channel, queueName: string): void {
	channel.assertQueue(queueName, {
		durable: false
	})
}
