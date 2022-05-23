import amqp, { Connection, Channel, Options, Message } from 'amqplib/callback_api';
import { v4 as uuidv4 } from 'uuid';

import dotenv from 'dotenv';
dotenv.config();

type Process = {
	id: string,
	messages: WrappedMessage[],
	sucessFnCallback: (payload?: any) => any
	failedFnCallback: (error?: any) => any,
	receivedMessages: WrappedMessage[]
}

type WrappedMessage = {
	processId: string,
	messageId: string,
	payload: any
}

export default class RabbitServer {
	private connectionConfig!: Options.Connect;
	private connection!: Connection;
	private channel!: Channel

	// Process UUID --> Process Data
	private processes!: Map<string, Process>;

	constructor(connectionConfig: Options.Connect) {
		this.connectionConfig = connectionConfig;
	}

	public async initialize() {
		this.processes = new Map();

		this.connection = await this.connectToQueue(this.connectionConfig);
		this.channel = await this.createChannel(this.connection);

		this.joinOrCreateQueue(this.channel, process.env.RABBIT_RESPONSE_QUEUE!);

		// Handles incoming messages
		this.channel.consume(process.env.RABBIT_RESPONSE_QUEUE!, this.handleIncomingMessage.bind(this), { noAck: true });
	}

	public joinQueue(queueName: string) {
		this.joinOrCreateQueue(this.channel, queueName);
	}

	public processAndQueueMessage(queue: string, messages: Buffer[], sucessFnCallback: (payload?: any) => any, failedFnCallback: (error?: any) => any) {
		const processId = uuidv4();

		const parsedMessages: WrappedMessage[] = messages.map((message, index) => ({
			processId,
			messageId: `${index}`,
			payload: message.toString('base64')
		}));

		const processData: Process = {
			id: processId,
			messages: parsedMessages,
			receivedMessages: [],
			sucessFnCallback,
			failedFnCallback
		}
		// console.log('ProcessData', processData);
		
		// El message tiene que estar compuesto por: el id proceso, id mensaje, y los datos del mensaje.

		// map<idProceso; { [idMensajes], sucessFnCallback; failedFnCallback, [mensajesRecibidos] }>
		// map <idMensaje, timeout>
		this.processes.set(processId, processData);

		// Queue messages
		for (const message of parsedMessages) {
			const stringifyMessage = JSON.stringify(message);
			console.log(`[SERVER] Sending chunck (Message ID ${message.messageId})`);
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
		channel.assertQueue(queueName, { durable: false });
	}

	private handleIncomingMessage(rawMessage: Message | null) {
		if (!rawMessage) return;

		const message: WrappedMessage = JSON.parse(rawMessage.content.toString());
		console.log(`[SERVER] Chunk received (Message ID ${message.messageId})`);
		// Gets the incoming message's process
		const process = this.processes.get(message.processId);
		if (!process) return;

		// Saves the received message
		process.receivedMessages.push(message);

		// Checks if the process has received all the messages
		const isProcessComplete = this.isProcessComplete(process);
		// console.log('isProcessComplete', isProcessComplete);

		if (isProcessComplete) {
			this.handleCompleteProcess(message.processId);
			// Deletes the entry from the process map
			this.processes.delete(message.processId);
		}
	}

	private isProcessComplete(process: Process) {
		if (process.messages.length !== process.receivedMessages.length) return false;
		// Checks that every message has been received
		return process.messages.every(message =>
			Boolean(process.receivedMessages.find(receiveMessage => message.messageId === receiveMessage.messageId))
		)
	}

	private handleCompleteProcess(processId: string) {
		const process = this.processes.get(processId);
		if (!process) return;

		// Sort received messages
		process.receivedMessages.sort((a, b) => {
			const indexOfA = process.messages.findIndex(message => message.messageId === a.messageId);
			const indexOfB = process.messages.findIndex(message => message.messageId === b.messageId);
			if (indexOfA > indexOfB) return 1;
			if (indexOfA < indexOfB) return -1;
			return 0;
		});

		process.sucessFnCallback(process.receivedMessages.map(message => Buffer.from(message.payload, 'base64')));
	}
}