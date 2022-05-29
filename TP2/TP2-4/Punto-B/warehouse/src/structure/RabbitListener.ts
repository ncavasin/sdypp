import amqp, { Connection, Channel, Options, Message } from 'amqplib/callback_api';

import dotenv from 'dotenv';
dotenv.config();

const Log = (...args: any[]) => {
	console.log(`[RABBIT-LISTENER]:`, ...args);
}

const LogError = (...args: any[]) => {
	Log(`[ERROR]`, ...args);
}

export default class RabbitListener {
	private connectionConfig!: Options.Connect;
	private connection!: Connection;
	private channel!: Channel

	constructor(config: Options.Connect) {
		this.connectionConfig = config;
	}

	public async initialize() {
		this.connection = await this.connectToQueue(this.connectionConfig);
		this.channel = await this.createChannel(this.connection);

		this.joinOrCreateQueue(this.channel, process.env.RABBIT_RESPONSE_QUEUE!);
	}

	public onIncomingMessage(ackFn: (msg: Message | null) => void) {
		// Handles incoming messages
		this.channel.consume(process.env.RABBIT_RESPONSE_QUEUE!, async rawMessage => {
			if (!rawMessage) return;
			try {
				// Executes the callback
				await ackFn(rawMessage);
				// Informs the channel with a success ack
				this.channel.ack(rawMessage);
			} catch (error) {
				// Informs the channel with a failed ack
				LogError('Failed to receive the message!', error);
				this.channel.nack(rawMessage);
			}
		}, { noAck: false });
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
		channel.assertQueue(queueName, { durable: false });
	}
}