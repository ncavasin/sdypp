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
const callback_api_1 = __importDefault(require("amqplib/callback_api"));
const dotenv_1 = __importDefault(require("dotenv"));
dotenv_1.default.config();
const Log = (...args) => {
    console.log(`[RABBIT-SERVER]`, ...args);
};
const LogError = (...args) => {
    Log(`- [ERROR]`, ...args);
};
class RabbitServer {
    constructor(connectionConfig) {
        this.connectionConfig = connectionConfig;
    }
    initialize() {
        return __awaiter(this, void 0, void 0, function* () {
            this.processes = new Map();
            this.connection = yield this.connectToQueue(this.connectionConfig);
            this.channel = yield this.createChannel(this.connection);
        });
    }
    joinQueue(queueName) {
        this.joinOrCreateQueue(this.channel, queueName);
    }
    QueueMessages(queue, messages) {
        for (const message of messages) {
            const stringifyMessage = JSON.stringify(message);
            Log(`Sending message (ID ${message.messageId})`);
            this.channel.sendToQueue(queue, Buffer.from(stringifyMessage));
        }
    }
    connectToQueue(config) {
        return new Promise((res, rej) => {
            callback_api_1.default.connect(config, function (error, connection) {
                if (error)
                    return rej(error);
                return res(connection);
            });
        });
    }
    createChannel(connection) {
        return new Promise((res, rej) => {
            connection.createChannel(function (error, channel) {
                if (error)
                    return rej(error);
                return res(channel);
            });
        });
    }
    joinOrCreateQueue(channel, queueName) {
        channel.assertQueue(queueName, { durable: false });
    }
}
exports.default = RabbitServer;
