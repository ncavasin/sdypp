import amqp, { Connection, Channel } from 'amqplib/callback_api';
import { createCanvas } from 'canvas';

const pixels = require('image-pixels');
const { Sobel } = require('sobel');

import dotenv from 'dotenv';
dotenv.config();

const Log = (...args: any[]) => {
	console.log(`[WORKER]`, ...args);
}

const LogError = (...args: any[]) => {
	Log(`- [ERROR]`, ...args);
}


const config = {
	protocol: process.env.RABBIT_PROTOCOL,
	hostname: process.env.RABBIT_HOST,
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

		Log('Sucessfully connected to RabbitMQ service');
		Log('Waiting for messages...');

		channel.consume(requestQueue, async function(rawMessage: any) {
			try {
				const message = JSON.parse(rawMessage.content.toString());
	
				Log(`Processing received message... (Message ID ${message.messageId})`);

				const requestPayloadBuffer = Buffer.from(message.payload, 'base64');

				const sobelImageBuffer = await processSobelFilter(requestPayloadBuffer);

				const responsePayloadBuffer = sobelImageBuffer.toString('base64');
				message.payload = responsePayloadBuffer;
	
				channel.sendToQueue(responseQueue, Buffer.from(JSON.stringify(message)));
				Log(`Processing completed! (Message ID ${message.messageId})`);
				// Informs the channel with a success ack
				channel.ack(rawMessage);
			} catch (error) {
				// Informs the channel with a failed ack
				LogError('Failed to process message', error)
				channel.nack(rawMessage);
			}
		}, {
			noAck: false
		});
	} catch (error) {
		LogError('Something went wrong!', error);
	}
}

async function processSobelFilter(image: Buffer) {
	// Transform the image buffer into an Array of Pixels
	const { data, width, height } = await pixels(image);

	// Apply the sobel filter over the Array of Pixels
	const sobel = Sobel({ data, width, height });
	const sobelImageData = await sobel.toImageData();

	// Creates the canvas to get the new Image Buffer
	const canvas = createCanvas(width, height);
	const context = canvas.getContext('2d');
	const imageData = context.createImageData(width, height);

	for (let i = 0; i < imageData.data.length; i += 4) {
		imageData.data[i + 0] = sobelImageData.data[i + 0];
		imageData.data[i + 1] = sobelImageData.data[i + 1];
		imageData.data[i + 2] = sobelImageData.data[i + 2];
		imageData.data[i + 3] = sobelImageData.data[i + 3];
	}

	context.putImageData(imageData, 0, 0);
	// Gets the new Image Buffer
	const sobelImageBuffer = canvas.toBuffer("image/png");

	return sobelImageBuffer;
}

function asyncTimeout() {
	return new Promise((res, rej) => {
		setTimeout(() => {
			return res(null);
		}, 5000)
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
		durable: true
	})
}
