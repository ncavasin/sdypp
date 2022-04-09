import { Socket } from "socket.io";
import Queue, { Message } from "./Queue";
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

	get queuedMessages(): Queue | undefined {
		if (!this.name) return;

		return this.server.getUserInboundMessages(this.name)!;
	}

	setName(name: string) {
		this._name = name;
	}

	private initializeEvent() {
		// Event declations goes here
		this.socket.on('setName', this.handleSetName.bind(this));
		this.socket.on('sendMessage', this.handleSendMessage.bind(this));
		this.socket.on('disconnect', this.handleDisconnect.bind(this));
		this.socket.on('retrieveQueuedMessages', this.handleRetrieveQueuedMessages.bind(this));
	}

	private handleSetName(name: string, ackCallback: (error?: string, message?: string) => void) {
		// A name can only be used in one connection
		// Check if it's already in use
		const nameInUse = this.server.isNameInUse(name);
		if (nameInUse) return ackCallback('Name already in use');

		this.setName(name);

		// Check if there are already inbound messages queue to this name
		const hasExistingQueue = this.queuedMessages;
		// If there are no messages to this user --> create the messages queue
		if (!hasExistingQueue) this.server.createInboundMessagesQueue(this.name!);

		return ackCallback(undefined, 'Name successfully set');
	}

	private handleSendMessage(destinatory: string, message: string) {
		if (!destinatory || !this.name) return;
		this.server.sendMessageToUser(destinatory, { message, sender: this.name, date: new Date() });
	}

	private handleDisconnect() {
		this.server.deleteConnection(this.connectionUuid);
	}

	private handleRetrieveQueuedMessages(ackCallback: (error?: string, payload?: any) => void) {
		if (!this.name) return ackCallback('A name has not been set yet');

		const queuedMessagesCopy: Queue = this.queuedMessages!.clone();

		const allMessages: Message[] = [];

		while (!queuedMessagesCopy.isEmpty()) {
			const shiftedItem = queuedMessagesCopy.pop();
			allMessages.push(shiftedItem!);
		}

		return ackCallback(undefined, allMessages);
	}

	public disconnectSocket() {
		this.socket.disconnect();
	}

	public sendMessage(message: Message) {
		this.socket.emit('messageReceived', message)
	}
}