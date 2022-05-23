import express, { Express, Request, Response } from 'express';
import fileUpload, { UploadedFile } from 'express-fileupload';

import fs from 'fs';

const pixels = require('image-pixels');
const { Sobel } = require('sobel');
const imageOutput = require('image-output');

import dotenv from 'dotenv';
dotenv.config();

export default class Server {
	private _express!: Express;

	initialize() {
		this._express = express();

		this._express.use(fileUpload());

		this.initializeEndpoints();

		const port = process.env.PORT || 4001;
		this._express.listen(port, () => {
			console.log(`Server running on port ${port}`);
		})
	}

	initializeEndpoints() {
		this._express.get('/', this.handleHomeRequest.bind(this));
		this._express.post('/sobel', this.handleSobelImageRequest.bind(this));
	}

	private handleHomeRequest(req: Request, res: Response) {
		return res.json({ message: "Centralized Sobel Server is online" });
	}

	private async handleSobelImageRequest(req: Request, res: Response) {
		try {
			const files = req.files;
			if (!files || Object.keys(files).length === 0 || !files.image) return res.status(400).json({ message: 'No image given' });
	
			const { image } = files as any;
			const { mimetype } = image;
	
			const start = new Date();
			const sobelImageData = await this.processSobelFilter(image);
			const end = new Date();
	
			console.log(`[Server] Image name: "${image.name}" --> Time: ${Number(end) - Number(start)}ms`);
	
			return res.contentType(mimetype).send(sobelImageData);
		} catch (error) {
			console.error(error);
			return res.status(500).json({ message: 'Ups!! Something went wrong!' });
		}
	}

	async processSobelFilter(image: UploadedFile) {
		// Transform the image buffer into an Array of Pixels
		const { data, width, height } = await pixels(image.data);

		// Apply the sobel filter over the Array of Pixels
		const sobel = Sobel({ data, width, height });
		const sobelImageData = await sobel.toImageData();

		const filename = `sobel-${image.name}`;

		// Saves the Array of Pixeles into a image file
		await imageOutput(sobelImageData, filename);
		// Reads the file to get the binary code
		const sobelImageBuffer = fs.readFileSync(filename);
		// Deletes the persisted file
		fs.unlinkSync(filename);

		return sobelImageBuffer;
	}
}