import ExpressServer, { Express } from 'express';
import Http, { Server as HttpServer} from "http";
import { Server as SocketIO, Socket } from "socket.io";
import path from 'path';
import dotenv from 'dotenv'

dotenv.config()

import SocketConnection from './SocketConnection';

export default class Server {
	private express: Express;
	private http: HttpServer;
	private io: SocketIO;

	constructor() {
		this.express = ExpressServer();
		this.http = new Http.Server(this.express);
		this.io = new SocketIO(this.http);
	}

	public initialize() {
		this.registerServerRoutes();
		this.registerSocketEvents();

		// Registrar los eventos
		const port = process.env.PORT || 3001
		this.http.listen(3001, ()=> {
			console.log(`Listening on port ${port}`);
		});
	}

	private registerServerRoutes() {
		// Home route
		this.express.get('/', (req, res) => {
			res.sendFile(path.join(__dirname, '..', '..', 'public', 'index.html'))
		})
	}

	private registerSocketEvents() {
		// New connection event
		this.io.on("connect", this.handleNewConnection.bind(this));
	}

	private handleNewConnection(socket: Socket) {
		console.log('new connection');
		const socketConnection = new SocketConnection(socket);
	}
}