import { Socket } from "socket.io";

export default class SocketConnection {
	private _socket: Socket;

	constructor(socket: Socket) {
		this._socket = socket;
		this.initializeEvent();
	}

	get socket(): Socket {
		return this._socket;
	}

	private initializeEvent() {
		// Event declations goes here
		this.socket.on('sendMessage', this.handleSendMessage.bind(this));
		this.socket.on('disconnect', this.handleDisconnect.bind(this));
	}

	private handleSendMessage(message: string) {
		console.log(`Un cliente mandó un mensaje: ${message}`);
		
		this.socket.emit('messageResponse', message);
	}

	private handleDisconnect() {
		console.log('connection terminated!');
	}
}