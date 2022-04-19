"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class SocketConnection {
    constructor(socket) {
        this._socket = socket;
        this.initializeEvent();
    }
    get socket() {
        return this._socket;
    }
    initializeEvent() {
        // Event declations goes here
        this.socket.on('sendMessage', this.handleSendMessage.bind(this));
        this.socket.on('disconnect', this.handleDisconnect.bind(this));
    }
    handleSendMessage(message) {
        this.socket.emit('messageResponse', message);
    }
    handleDisconnect() {
        // console.log('connection terminated!');
    }
}
exports.default = SocketConnection;
