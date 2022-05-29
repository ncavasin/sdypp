import { createClient, RedisClientType } from 'redis';
import Server, { Process, WrappedMessage } from './Server';

const Log = (...args: any[]) => {
	console.log(`[REDIS-SERVER]:`, ...args);
}

const LogError = (...args: any[]) => {
	Log(`[ERROR]`, ...args);
}


export type RedisCredencials = {
	username: string,
	password: string,
	host: string,
	port: number
}

export default class RedisServer {
	private _redisServer: RedisClientType;

	constructor(crendetials: RedisCredencials) {
		this._redisServer = createClient({
			username: crendetials.username,
			password: crendetials.password,
			socket: {
				port: crendetials.port,
				host: crendetials.host
			}
		});
	}

	public get instance() {
		return this._redisServer;
	}

	public async initialize() {
		this._redisServer.on('error', (err) => {
			LogError('Error occured while connecting or accessing redis server', err);
		});

		await this._redisServer.connect();
	}

	public async setProcess(id: string, process: Process) {
		return this._redisServer.set(`process:${id}`, JSON.stringify(process));
	}

	public async getProcess(processId: string): Promise<Process | null> {
		const process = await this._redisServer.get(`process:${processId}`);
		if (!process) return null;
		return JSON.parse(process);
	}

	public async setProcessFullImage(processId: string, imageBuffer: Buffer) {
		return this._redisServer.set(`fullImage:${processId}`, imageBuffer.toString('base64'));
	}

	public async getProcessFullImage(processId: string): Promise<Buffer | null> {
		const fullImage = await this._redisServer.get(`fullImage:${processId}`);
		if (!fullImage) return null;

		return Buffer.from(fullImage, 'base64');
	}

	public async addReceivedMessage(processId: string, message: WrappedMessage) {
		const process = await this.getProcess(processId);
		if (!process) throw Error('Process not found!');

		await this._redisServer.rPush(`receivedMessages:${processId}`, JSON.stringify(message));
	}

	public async getReceivedMessages(processId: string): Promise<WrappedMessage[]> {
		const process = await this.getProcess(processId);
		if (!process) throw Error('Process not found!');

		const receivedMessages = await this._redisServer.lRange(`receivedMessages:${processId}`, 0, process.messagesId.length);
		const parsedReceivedMessages: WrappedMessage[] = receivedMessages.map(rawMessage => JSON.parse(rawMessage));
		return parsedReceivedMessages;
	}
}