import ExpressServer, { Express } from 'express';
import Http, { Server as HttpServer} from "http";
import { Server as SocketIO, Socket } from "socket.io";
import path from 'path';
import dotenv from 'dotenv';
import { v4 as uuid } from 'uuid';

dotenv.config()

import SocketConnection from './SocketConnection';
import Queue, { Message } from './Queue';

export default class Server {
	private express: Express;
	private http: HttpServer;
	private io: SocketIO;

	private connections: Map<string, SocketConnection>;
	private userMessages: Map<string, Queue>;

	constructor() {
		this.express = ExpressServer();
		this.http = new Http.Server(this.express);
		this.io = new SocketIO(this.http);

		this.connections = new Map();
		this.userMessages = new Map();
	}

	public initialize(): void {
		this.registerServerRoutes();
		this.registerSocketEvents();

		// Registrar los eventos
		const port = process.env.PORT || 3001
		this.http.listen(port, ()=> {
			console.log(`Listening on port ${port}`);
		});
	}

	private registerServerRoutes(): void {
		// Home route
		this.express.get('/', (req, res) => {
			res.sendFile(path.join(__dirname, '..', '..', 'public', 'index.html'))
		});
	}

	private registerSocketEvents(): void {
		// New connection event
		this.io.on("connect", this.handleNewConnection.bind(this));
	}

	private handleNewConnection(socket: Socket): void {
		const connectionUuid = uuid();
		const socketConnection = new SocketConnection(socket, connectionUuid, this);
		this.addConnection(connectionUuid, socketConnection)
	}

	public addConnection(uuid: string, socketConnection: SocketConnection): void {
		this.connections.set(uuid, socketConnection);
	}

	public deleteConnection(uuid: string, forceSocketDisconnection: boolean = false): void {
		if (forceSocketDisconnection) {
			// before deleting the reference to the socket, disconnect it
			const connection = this.connections.get(uuid);
			if (connection) connection.disconnectSocket();
		}

		this.connections.delete(uuid);
	}

	public isNameInUse(name: string): boolean {
		return Boolean(this.getUserConnectionByName(name));
	}

	public getUserConnectionByName(name: string) {
		return Array.from(this.connections.values()).find(socketConnection => socketConnection.name === name)
	}

	public sendMessageToUser(destinatory: string, message: Message): void {
		// Send message to user --> search for user connection & send message
		const userConnection = this.getUserConnectionByName(destinatory);
		if (userConnection) userConnection.sendMessage(message);
		// Add message to the user Queue
		const userQueue = this.userMessages.get(destinatory);
		if (userQueue) {
			// Push the incoming message to the user's queue
			userQueue.push(message);
		} else {
			// Creates a new Queue instance
			const newQueue = new Queue();
			// Push the incoming message to the new user's queue
			newQueue.push(message);
			this.userMessages.set(destinatory, newQueue);
		}
	}

	public getUserInboundMessages(userName: string): Queue | undefined {
		return this.userMessages.get(userName);
	}

	public createInboundMessagesQueue(userName: string) {
		this.userMessages.set(userName, new Queue());
	}
}