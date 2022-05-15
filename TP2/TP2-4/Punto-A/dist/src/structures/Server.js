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
const express_fileupload_1 = __importDefault(require("express-fileupload"));
const fs_1 = __importDefault(require("fs"));
const pixels = require('image-pixels');
const imageOutput = require('image-output');
const { Sobel } = require('sobel');
const dotenv_1 = __importDefault(require("dotenv"));
dotenv_1.default.config();
class Server {
    initialize() {
        this._express = (0, express_1.default)();
        this._express.use((0, express_fileupload_1.default)());
        this.initializeEndpoints();
        const port = process.env.PORT || 4002;
        this._express.listen(port, () => {
            console.log(`Server running on port ${port}`);
        });
    }
    initializeEndpoints() {
        this._express.get('/', this.handleHomeRequest.bind(this));
        this._express.post('/sobel', this.handleSobelImageRequest.bind(this));
    }
    handleHomeRequest(req, res) {
        return res.json({ message: "Centralized Sobel Server is online" });
    }
    handleSobelImageRequest(req, res) {
        return __awaiter(this, void 0, void 0, function* () {
            const files = req.files;
            if (!files || Object.keys(files).length === 0 || !files.image)
                return res.status(400).json({ message: 'No image given' });
            const { image } = files;
            const { mimetype } = image;
            const start = new Date();
            const sobelImageData = yield this.processSobelFilter(image);
            const end = new Date();
            console.log(`[Server] Image name: "${image.name}" --> Time: ${Number(end) - Number(start)}ms`);
            return res.contentType(mimetype).send(sobelImageData);
        });
    }
    processSobelFilter(image) {
        return __awaiter(this, void 0, void 0, function* () {
            // Transform the image buffer into an Array of Pixels
            const { data, width, height } = yield pixels(image.data);
            // Apply the sobel filter over the Array of Pixels
            const sobel = Sobel({ data, width, height });
            const sobelImageData = yield sobel.toImageData();
            const filename = `sobel-${image.name}`;
            // Saves the Array of Pixeles into a image file
            yield imageOutput(sobelImageData, filename);
            // Reads the file to get the binary code
            const sobelImageBuffer = fs_1.default.readFileSync(filename);
            // Deletes the persisted file
            fs_1.default.unlinkSync(filename);
            return sobelImageBuffer;
        });
    }
}
exports.default = Server;
