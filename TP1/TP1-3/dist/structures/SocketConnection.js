"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class SocketConnection {
    constructor(socket, uuid, server) {
        this._server = server;
        this._socket = socket;
        this._uuid = uuid;
        this.initializeEvent();
    }
    get server() {
        return this._server;
    }
    get socket() {
        return this._socket;
    }
    get connectionUuid() {
        return this._uuid;
    }
    get name() {
        return this._name;
    }
    get queuedMessages() {
        if (!this.name)
            return;
        return this.server.getUserInboundMessages(this.name);
    }
    setName(name) {
        this._name = name;
    }
    initializeEvent() {
        // Event declations goes here
        this.socket.on('setName', this.handleSetName.bind(this));
        this.socket.on('sendMessage', this.handleSendMessage.bind(this));
        this.socket.on('disconnect', this.handleDisconnect.bind(this));
        this.socket.on('retrieveQueuedMessages', this.handleRetrieveQueuedMessages.bind(this));
    }
    handleSetName(name, ackCallback) {
        // A name can only be used in one connection
        // Check if it's already in use
        const nameInUse = this.server.isNameInUse(name);
        if (nameInUse)
            return ackCallback('Name already in use');
        this.setName(name);
        // Check if there are already inbound messages queue to this name
        const hasExistingQueue = this.queuedMessages;
        // If there are no messages to this user --> create the messages queue
        if (!hasExistingQueue)
            this.server.createInboundMessagesQueue(this.name);
        return ackCallback(undefined, 'Name successfully set');
    }
    handleSendMessage(destinatory, message) {
        if (!destinatory || !this.name)
            return;
        this.server.sendMessageToUser(destinatory, { message, sender: this.name, date: new Date() });
    }
    handleDisconnect() {
        this.server.deleteConnection(this.connectionUuid);
    }
    handleRetrieveQueuedMessages(ackCallback) {
        if (!this.name)
            return ackCallback('A name has not been set yet');
        const queuedMessagesCopy = this.queuedMessages.clone();
        const allMessages = [];
        while (!queuedMessagesCopy.isEmpty()) {
            const shiftedItem = queuedMessagesCopy.pop();
            allMessages.push(shiftedItem);
        }
        return ackCallback(undefined, allMessages);
    }
    disconnectSocket() {
        this.socket.disconnect();
    }
    sendMessage(message) {
        this.socket.emit('messageReceived', message);
    }
}
exports.default = SocketConnection;
