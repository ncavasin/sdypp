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
const fs_1 = __importDefault(require("fs"));
const path_1 = __importDefault(require("path"));
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
                // Endpoints
                this.initializeEndpoints();
                const port = process.env.PORT || 4001;
                this._express.listen(port, () => {
                    Log(`server running on port ${port}`);
                });
            }
            catch (error) {
                LogError('Something went wrong!', error);
                process.exit(1);
            }
        });
    }
    initializeEndpoints() {
        this._express.get('/', this.handleHomeRequest.bind(this));
        this._express.get('/doMath', this.handleDoMath.bind(this));
        this._express.post('/doSomething', this.handleDoSomethingRequest.bind(this));
    }
    handleHomeRequest(req, res) {
        return res.json({ message: "Server is online" });
    }
    handleDoMath(req, res) {
        let x = 0;
        for (let i = 1; i < 1000; i++) {
            x += i * i / i;
        }
        console.log(x);
        return res.json({ message: "Math was done successfully" });
    }
    handleDoSomethingRequest(req, res) {
        var _a, _b;
        return __awaiter(this, void 0, void 0, function* () {
            try {
                this.executeInBackground((_b = (_a = req.body) === null || _a === void 0 ? void 0 : _a.ms) !== null && _b !== void 0 ? _b : 10000);
                res.status(200).json({ message: 'Something was done successfully' });
            }
            catch (error) {
                LogError('Failed to do something', error);
                return res.status(500).json({ message: 'Ups!! Something went wrong!' });
            }
        });
    }
    setTimeoutSync(ms) {
        return new Promise((res, rej) => {
            setTimeout(() => {
                return res('');
            }, ms);
        });
    }
    executeInBackground(msTimes) {
        return __awaiter(this, void 0, void 0, function* () {
            let someArray = [];
            let pathDir = path_1.default.join(__dirname, '..', '..', 'images', 'auto.jpg');
            if (!fs_1.default.existsSync(pathDir)) {
                pathDir = path_1.default.join(__dirname, '..', '..', '..', 'images', 'auto.jpg');
            }
            // The img size is 3,34 MB
            // Fetching the image 10 times would make the server hold 33,4 MB in memory per request for 10 seconds
            for (let i = 0; i < 10; i++) {
                fs_1.default.readFile(pathDir, ((error, data) => {
                    if (error)
                        console.error(error);
                    if (data)
                        someArray.push(data);
                }));
            }
            console.log(`Holding 33,4 MB in memory for ${msTimes}ms`);
            yield this.setTimeoutSync(msTimes);
            console.log('Deleting 33.4 MB from memory');
        });
    }
}
exports.default = Server;
