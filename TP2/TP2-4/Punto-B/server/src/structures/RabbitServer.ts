import amqp, { Connection, Channel, Options } from 'amqplib/callback_api';

import dotenv from 'dotenv';
dotenv.config();

const Log = (...args: any[]) => {
	console.log(`[RABBIT-SERVER]`, ...args);
}

const LogError = (...args: any[]) => {
	Log(`- [ERROR]`, ...args);
}


export type Process = {
	id: string,
	messages: WrappedMessage[],
	receivedMessages: WrappedMessage[]
}

export type WrappedMessage = {
	processId: string,
	messageId: string,
	payload: any
}

export default class RabbitServer {
	private connectionConfig!: Options.Connect;
	private connection!: Connection;
	private channel!: Channel

	constructor(connectionConfig: Options.Connect) {
		this.connectionConfig = connectionConfig;
	}

	public async initialize() {
		this.connection = await this.connectToQueue(this.connectionConfig);
		this.channel = await this.createChannel(this.connection);
	}

	public joinQueue(queueName: string) {
		this.joinOrCreateQueue(this.channel, queueName);
	}

	public QueueMessages(queue: string, messages: WrappedMessage[]) {
		for (const message of messages) {
			const stringifyMessage = JSON.stringify(message);
			Log(`Sending message (ID ${message.messageId})`);
			this.channel.sendToQueue(queue, Buffer.from(stringifyMessage));
		}
	}

	private connectToQueue(config: any): Promise<Connection> {
		return new Promise((res, rej) => {
			amqp.connect(config, function (error, connection) {
				if (error) return rej(error);
				return res(connection);
			});
		})
	}

	private createChannel(connection: Connection): Promise<Channel> {
		return new Promise((res, rej) => {
			connection.createChannel(function (error, channel) {
				if (error) return rej(error);
				return res(channel);
			});
		})
	}

	private joinOrCreateQueue(channel: Channel, queueName: string): void {
		channel.assertQueue(queueName, { durable: true });
	}
}