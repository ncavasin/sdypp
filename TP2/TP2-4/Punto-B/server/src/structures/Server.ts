import express, { Express, Request, Response } from 'express';
import bodyParser from 'body-parser';
import fileUpload, { UploadedFile } from 'express-fileupload';

const { imageToChunks } = require('split-images');
const { joinImages } = require('join-images');

import RabbitServer from './RabbitServer';

import dotenv from 'dotenv';
dotenv.config();

export default class Server {
	private _express!: Express;
	private _rabbitServer!: RabbitServer;

	async initialize() {
		try {
			// Express
			this._express = express();
			this._express.use(bodyParser.json());
			this._express.use(fileUpload());
			// RabbitMQ
			await this.initializeRabbitMQ();
			// Endpoints
			this.initializeEndpoints();

			const port = process.env.PORT || 4001;
			this._express.listen(port, () => {
				console.log(`Decentralized server running on port ${port}`);
			})
		} catch (error) {
			console.error('Something went wrong!!', error);
			process.exit(1);
		}
	}

	private async initializeRabbitMQ() {
		const config = {
			protocol: process.env.RABBIT_PROTOCOL,
			host: process.env.RABBIT_HOST,
			port: Number(process.env.RABBIT_PORT),
			username: process.env.RABBIT_USERNAME,
			password: process.env.RABBIT_PASSWORD
		};

		this._rabbitServer = new RabbitServer(config);
		await this._rabbitServer.initialize();
		this._rabbitServer.joinQueue(process.env.RABBIT_REQUEST_QUEUE!);
	}

	initializeEndpoints() {
		this._express.get('/', this.handleHomeRequest.bind(this));
		this._express.post('/sobel', this.handleSobelImageRequest.bind(this));
	}

	private handleHomeRequest(req: Request, res: Response) {
		return res.json({ message: "Decentralized Sobel Server is online" });
	}

	private async handleSobelImageRequest(req: Request, res: Response) {
		try {
			const files = req.files;
			if (!files || Object.keys(files).length === 0 || !files.image) return res.status(400).json({ message: 'No image given' });

			const { image } = files as any;
			const { mimetype } = image;
	
			const start = new Date();

			const chunckHeightSize = 1000;

			const chuncks: Buffer[] = await imageToChunks(image.data, chunckHeightSize);
			console.log(`[SERVER] Process started! - File: "${image.name}" - Chunks: ${chuncks.length}`);
			
			const sucessFnCallback = async (payload: any) => {
				const end = new Date();
				const totalTime = Number(end) - Number(start);
				console.log(`[SERVER] Process completed! - File: "${image.name}" - Chunks merged: ${chuncks.length} - Time: ${totalTime}ms`);

				const mergedImgSharp = await this.joinImagesAsync(payload);
				const bufferImg = await mergedImgSharp.png().toBuffer();

				return res.contentType(mimetype).send(bufferImg);
			}

			const failedFnCallback = () => {
				return res.status(500).json({ message: 'Algo pasó y no se pudo ejecutar correctamente' });
			}

			this._rabbitServer.processAndQueueMessage(process.env.RABBIT_REQUEST_QUEUE!, chuncks, sucessFnCallback, failedFnCallback);
		} catch (error) {
			console.error(error);
			return res.status(500).json({ message: 'Ups!! Something went wrong!' });
		}
	}

	joinImagesAsync(images: Buffer[]): any {
		return new Promise((res, rej) => {
			joinImages(images)
				.then((img: any) => {
					res(img);
				})
				.catch((error: Error) => {
					return rej(error);
				})
			
		})
	}

	logReceptionMessage(imageName: string, timeMS: number, totalChuncks: number) {
		console.log(`
----------------------------------------------------------
[Server] Process completed!
  - Image name: "${imageName}"
  - Time: ${timeMS}ms
  - ${totalChuncks} chuncks merged
----------------------------------------------------------
		`);
	}

	// async processSobelFilter(image: UploadedFile) {
	// 	// Transform the image buffer into an Array of Pixels
	// 	const { data, width, height } = await pixels(image.data);

	// 	// Apply the sobel filter over the Array of Pixels
	// 	const sobel = Sobel({ data, width, height });
	// 	const sobelImageData = await sobel.toImageData();

	// 	const filename = `sobel-${image.name}`;

	// 	// Saves the Array of Pixeles into a image file
	// 	await imageOutput(sobelImageData, filename);
	// 	// Reads the file to get the binary code
	// 	const sobelImageBuffer = fs.readFileSync(filename);
	// 	// Deletes the persisted file
	// 	fs.unlinkSync(filename);

	// 	return sobelImageBuffer;
	// }
}