import { Socket } from "socket.io";
import Server from "./Server";

export default class SocketConnection {
	private _server: Server;
	private _socket: Socket;
	private _uuid: string;
	private _name?: string;

	constructor(socket: Socket, uuid: string, server: Server) {
		this._server = server;
		this._socket = socket;
		this._uuid = uuid;

		this.initializeEvent();
	}

	get server(): Server {
		return this._server;
	}

	get socket(): Socket {
		return this._socket;
	}

	get connectionUuid(): string {
		return this._uuid;
	}

	get name() {
		return this._name;
	}

	setName(name: string) {
		this._name = name;
	}

	private initializeEvent() {
		// Event declations goes here
		this.socket.on('setName', this.handleSetName.bind(this));
		this.socket.on('sendMessage', this.handleSendMessage.bind(this));
		this.socket.on('disconnect', this.handleDisconnect.bind(this));
	}

	private handleSetName(name: string, ackCallback: (error?: string, message?: string) => void) {
		// A name can only be used in one connection
		// Check if it's already in use
		const nameInUse = this.server.isNameInUse(name);
		if (nameInUse) return ackCallback('Name already in use');

		this.setName(name);
		return ackCallback(undefined, 'Name successfully set');
	}

	private handleSendMessage(message: string) {
		if (!this.name) return;

		console.log(`${this.name} mandó un mensaje: ${message}`);
		this.socket.emit('messageResponse', message);
	}

	private handleDisconnect() {
		this.server.deleteConnection(this.connectionUuid);
	}

	public disconnectSocket() {
		this.socket.disconnect();
	}
}