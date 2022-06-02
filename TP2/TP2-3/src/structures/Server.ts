import express, { Express, Request, Response } from 'express';
import axios from 'axios';

import dotenv from 'dotenv';

dotenv.config();

const Log = (...args: any[]) => {
	console.log(`[SERVER]`, ...args);
}

const LogError = (...args: any[]) => {
	Log(`- [ERROR]`, ...args);
}


export default class Server {
	private _express!: Express;

	async initialize() {
		try {
			// Express
			this._express = express();
			// Endpoints
			this.initializeEndpoints();

			const port = process.env.PORT || 4001;
			this._express.listen(port, () => {
				Log(`server running on port ${port}`);
			})
		} catch (error) {
			LogError('Something went wrong!', error);
			process.exit(1);
		}
	}


	initializeEndpoints() {
		this._express.get('/', this.handleHomeRequest.bind(this));
		this._express.post('/doSomething', this.handleDoSomethingRequest.bind(this));
	}

	private handleHomeRequest(req: Request, res: Response) {
		return res.json({ message: "Server is online" });
	}

	private async handleDoSomethingRequest(req: Request, res: Response) {
		try {
			this.executeInBackground();
			res.status(200).json({ message: 'Something was done successfully' });
		} catch (error) {
			LogError('Failed to do something', error);
			return res.status(500).json({ message: 'Ups!! Something went wrong!' });
		}
	}

	setTimeoutSync(ms: number) {
		return new Promise((res, rej) => {
			setTimeout(() => {
				return res('')
			}, ms)
		})
	}

	async executeInBackground() {
		const someArray = [];

		for (let i = 0; i <= 1000; i++) {
			axios.get('https://fondosmil.com/fondo/17009.jpg')
				.then((response => {
					console.log('se llamo al .then');
					someArray.push(response.data);
					// console.log(someArray.length);
				}))
				.catch((error) => {})
		}

		await this.setTimeoutSync(10000);
		console.log(someArray.length);
	}
}