"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const body_parser_1 = __importDefault(require("body-parser"));
const express_fileupload_1 = __importDefault(require("express-fileupload"));
const axios_1 = __importDefault(require("axios"));
const uuid_1 = require("uuid");
const { imageToChunks } = require('split-images');
const { joinImages } = require('join-images');
const RabbitServer_1 = __importDefault(require("./RabbitServer"));
const dotenv_1 = __importDefault(require("dotenv"));
dotenv_1.default.config();
const Log = (...args) => {
    console.log(`[SERVER]`, ...args);
};
const LogError = (...args) => {
    Log(`- [ERROR]`, ...args);
};
class Server {
    initialize() {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                // Express
                this._express = (0, express_1.default)();
                this._express.use(body_parser_1.default.json());
                this._express.use((0, express_fileupload_1.default)());
                // RabbitMQ
                yield this.initializeRabbitMQ();
                // Endpoints
                this.initializeEndpoints();
                const port = process.env.PORT || 4001;
                this._express.listen(port, () => {
                    Log(`Decentralized server running on port ${port}`);
                });
            }
            catch (error) {
                LogError('Something went wrong!', error);
                process.exit(1);
            }
        });
    }
    initializeRabbitMQ() {
        return __awaiter(this, void 0, void 0, function* () {
            const config = {
                protocol: process.env.RABBIT_PROTOCOL,
                host: process.env.RABBIT_HOST,
                port: Number(process.env.RABBIT_PORT),
                username: process.env.RABBIT_USERNAME,
                password: process.env.RABBIT_PASSWORD
            };
            this._rabbitServer = new RabbitServer_1.default(config);
            yield this._rabbitServer.initialize();
            this._rabbitServer.joinQueue(process.env.RABBIT_REQUEST_QUEUE);
        });
    }
    initializeEndpoints() {
        this._express.get('/', this.handleHomeRequest.bind(this));
        this._express.post('/sobel', this.handleSobelImageRequest.bind(this));
        this._express.get('/sobel/:processId', this.handleGetSobelImageRequest.bind(this));
    }
    handleHomeRequest(req, res) {
        return res.json({ message: "Decentralized Sobel Server is online" });
    }
    handleGetSobelImageRequest(req, res) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const { processId } = req.params;
                if (!processId)
                    return res.status(400).json('params.processId is required!');
                const response = yield axios_1.default.get(`${process.env.WAREHOUSE_HOST}:${process.env.WAREHOUSE_PORT}/process/${processId}`, {
                    responseType: 'arraybuffer'
                });
                res.setHeader('Content-Type', response.headers['content-type']);
                return res.send(response.data);
            }
            catch (error) {
                LogError('Failed to get image', error);
                return res.status(500).json({ message: 'Ups!! Something went wrong!' });
            }
        });
    }
    handleSobelImageRequest(req, res) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const files = req.files;
                if (!files || Object.keys(files).length === 0 || !files.image)
                    return res.status(400).json({ message: 'No image given' });
                const { image } = files;
                const { name, mimetype } = image;
                // Max height size of image chunks
                const chunckHeightSize = 1000;
                const chuncks = yield imageToChunks(image.data, chunckHeightSize);
                Log(`Process started! - File: "${image.name}" - Messages: ${chuncks.length}`);
                const createdProcess = this.createProcess(chuncks);
                yield axios_1.default.post(`${process.env.WAREHOUSE_HOST}:${process.env.WAREHOUSE_PORT}/process/`, {
                    process: {
                        id: createdProcess.id,
                        messages: createdProcess.messages.map(message => message.messageId),
                        name,
                        mimetype
                    }
                });
                // Send the messages to the workers
                this._rabbitServer.QueueMessages(process.env.RABBIT_REQUEST_QUEUE, createdProcess.messages);
                return res.json({ message: 'The image successfully upload', processId: createdProcess.id });
            }
            catch (error) {
                LogError(error);
                return res.status(500).json({ message: 'Ups!! Something went wrong!' });
            }
        });
    }
    createProcess(messages) {
        const processId = (0, uuid_1.v4)();
        const parsedMessages = messages.map((message, index) => ({
            processId,
            messageId: `${index}`,
            payload: message.toString('base64')
        }));
        const processData = {
            id: processId,
            messages: parsedMessages,
            receivedMessages: []
        };
        return processData;
    }
    joinImagesAsync(images) {
        return new Promise((res, rej) => {
            joinImages(images)
                .then((img) => {
                res(img);
            })
                .catch((error) => {
                return rej(error);
            });
        });
    }
}
exports.default = Server;
