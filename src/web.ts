import { WebPlugin } from '@capacitor/core';
import { CapacitorQRCodeCameraXPlugin } from './definitions';

export class CapacitorQRCodeCameraXWeb extends WebPlugin implements CapacitorQRCodeCameraXPlugin {
	constructor() {
		super({
			name: 'CapacitorQRCodeCameraX',
			platforms: ['web']
		});
	}

	start(params: any): Promise<any> {
		return new Promise((resolve,reject) => {
			if(params.reject){
				return reject({"erro": "Ocorreu um erro desconhecido."});
			}
			return resolve({"status": "OK", "codigo": "0181"});
		});
	}
}

const CapacitorQRCodeCameraX = new CapacitorQRCodeCameraXWeb();

export { CapacitorQRCodeCameraX };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapacitorQRCodeCameraX);
