import express, { Express, Request, Response } from 'express';
import bodyParser from 'body-parser';
import axios from 'axios';
import fs from 'fs';
import path from 'path';

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
		this._express.get('/doMath', this.handleDoMath.bind(this));
		this._express.post('/doSomething', this.handleDoSomethingRequest.bind(this));
	}

	private handleHomeRequest(req: Request, res: Response) {
		return res.json({ message: "Server is online" });
	}

	private handleDoMath(req: Request, res: Response) {
		let x = 0;
		for (let i = 1; i < 1000; i++) {
			x += i * i / i;
		}
		console.log(x);
		return res.json({ message: "Math was done successfully" });
	}

	private async handleDoSomethingRequest(req: Request, res: Response) {
		try {
			this.executeInBackground(req.body?.ms ?? 10000);
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

	async executeInBackground(msTimes: number) {
		console.log('Se comenzó a reservar memoria');

		const arraySize = 5000;
		const bigArray = Array(arraySize);
		for (let i = 0; i < bigArray.length; i++) {
			bigArray[i] = Array(arraySize);
		}

		for (let i = 0; i < bigArray.length; i++) {
			for (let j = 0; j < bigArray.length; j++) {
				bigArray[i][j] = Math.random();
			}
		}

		let total = 0;
		for (let i = 0; i < bigArray.length; i++) {
			for (let j = 0; j < bigArray.length; j++) {
				total += bigArray[i][j];
			}
		}

		await this.setTimeoutSync(10000);
		console.log('Se liberó');
	}
}