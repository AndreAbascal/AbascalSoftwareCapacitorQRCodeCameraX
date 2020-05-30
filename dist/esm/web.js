import { WebPlugin } from '@capacitor/core';
export class CapacitorQRCodeCameraXWeb extends WebPlugin {
    constructor() {
        super({
            name: 'CapacitorQRCodeCameraX',
            platforms: ['web']
        });
    }
    start(params) {
        return new Promise((resolve, reject) => {
            if (params.reject) {
                return reject({ "erro": "Ocorreu um erro desconhecido." });
            }
            return resolve({ "status": "OK", "codigo": "0181" });
        });
    }
}
const CapacitorQRCodeCameraX = new CapacitorQRCodeCameraXWeb();
export { CapacitorQRCodeCameraX };
import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapacitorQRCodeCameraX);
//# sourceMappingURL=web.js.map