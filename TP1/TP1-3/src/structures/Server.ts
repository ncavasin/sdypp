import ExpressServer, { Express } from 'express';
import Http, { Server as HttpServer} from "http";
import { Server as SocketIO, Socket } from "socket.io";
import path from 'path';
import dotenv from 'dotenv';
import { v4 as uuid } from 'uuid';

dotenv.config()

import SocketConnection from './SocketConnection';

export default class Server {
	private express: Express;
	private http: HttpServer;
	private io: SocketIO;

	private connections: Map<string, SocketConnection>;

	constructor() {
		this.express = ExpressServer();
		this.http = new Http.Server(this.express);
		this.io = new SocketIO(this.http);

		this.connections = new Map();
	}

	public initialize(): void {
		this.registerServerRoutes();
		this.registerSocketEvents();

		// Registrar los eventos
		const port = process.env.PORT || 3001
		this.http.listen(3001, ()=> {
			console.log(`Listening on port ${port}`);
		});
	}

	private registerServerRoutes(): void {
		// Home route
		this.express.get('/', (req, res) => {
			res.sendFile(path.join(__dirname, '..', '..', 'public', 'index.html'))
		})
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
		console.log('Added connection! Total', this.connections.size);
	}

	public deleteConnection(uuid: string, forceSocketDisconnection: boolean = false): void {
		if (forceSocketDisconnection) {
			// before deleting the reference to the socket, disconnect it
			const connection = this.connections.get(uuid);
			if (connection) connection.disconnectSocket();
		}

		this.connections.delete(uuid);
		console.log('Deleted connection! Total', this.connections.size);
	}

	public isNameInUse(name: string): boolean {
		return Boolean(
			Array.from(this.connections.values()).find(socketConnection => socketConnection.name === name))
	}
}