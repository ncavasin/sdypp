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
dotenv_1.default.config();
const SocketConnection_1 = __importDefault(require("./SocketConnection"));
class Server {
    constructor() {
        this.express = (0, express_1.default)();
        this.http = new http_1.default.Server(this.express);
        this.io = new socket_io_1.Server(this.http);
    }
    initialize() {
        this.registerServerRoutes();
        this.registerSocketEvents();
        // Registrar los eventos
        const port = process.env.PORT || 3001;
        this.http.listen(3001, () => {
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
        const socketConnection = new SocketConnection_1.default(socket);
    }
}
exports.default = Server;
