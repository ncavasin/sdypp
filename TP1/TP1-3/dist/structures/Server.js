"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const http_1 = __importDefault(require("http"));
const socket_io_1 = require("socket.io");
const path_1 = __importDefault(require("path"));
const dotenv_1 = __importDefault(require("dotenv"));
const uuid_1 = require("uuid");
dotenv_1.default.config();
const SocketConnection_1 = __importDefault(require("./SocketConnection"));
const Queue_1 = __importDefault(require("./Queue"));
class Server {
    constructor() {
        this.express = (0, express_1.default)();
        this.http = new http_1.default.Server(this.express);
        this.io = new socket_io_1.Server(this.http);
        this.connections = new Map();
        this.userMessages = new Map();
    }
    initialize() {
        this.registerServerRoutes();
        this.registerSocketEvents();
        // Registrar los eventos
        const port = process.env.PORT || 3001;
        this.http.listen(port, () => {
            console.log(`Listening on port ${port}`);
        });
    }
    registerServerRoutes() {
        // Home route
        this.express.get('/', (req, res) => {
            res.sendFile(path_1.default.join(__dirname, '..', '..', 'public', 'index.html'));
        });
    }
    registerSocketEvents() {
        // New connection event
        this.io.on("connect", this.handleNewConnection.bind(this));
    }
    handleNewConnection(socket) {
        const connectionUuid = (0, uuid_1.v4)();
        const socketConnection = new SocketConnection_1.default(socket, connectionUuid, this);
        this.addConnection(connectionUuid, socketConnection);
    }
    addConnection(uuid, socketConnection) {
        this.connections.set(uuid, socketConnection);
    }
    deleteConnection(uuid, forceSocketDisconnection = false) {
        if (forceSocketDisconnection) {
            // before deleting the reference to the socket, disconnect it
            const connection = this.connections.get(uuid);
            if (connection)
                connection.disconnectSocket();
        }
        this.connections.delete(uuid);
    }
    isNameInUse(name) {
        return Boolean(this.getUserConnectionByName(name));
    }
    getUserConnectionByName(name) {
        return Array.from(this.connections.values()).find(socketConnection => socketConnection.name === name);
    }
    sendMessageToUser(destinatory, message) {
        // Send message to user --> search for user connection & send message
        const userConnection = this.getUserConnectionByName(destinatory);
        if (userConnection)
            userConnection.sendMessage(message);
        // Add message to the user Queue
        const userQueue = this.userMessages.get(destinatory);
        if (userQueue) {
            // Push the incoming message to the user's queue
            userQueue.push(message);
        }
        else {
            // Creates a new Queue instance
            const newQueue = new Queue_1.default();
            // Push the incoming message to the new user's queue
            newQueue.push(message);
            this.userMessages.set(destinatory, newQueue);
        }
    }
    getUserInboundMessages(userName) {
        return this.userMessages.get(userName);
    }
    createInboundMessagesQueue(userName) {
        this.userMessages.set(userName, new Queue_1.default());
    }
}
exports.default = Server;
