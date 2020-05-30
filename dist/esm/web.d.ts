import { WebPlugin } from '@capacitor/core';
import { CapacitorQRCodeCameraXPlugin } from './definitions';
export declare class CapacitorQRCodeCameraXWeb extends WebPlugin implements CapacitorQRCodeCameraXPlugin {
    constructor();
    start(params: any): Promise<any>;
}
declare const CapacitorQRCodeCameraX: CapacitorQRCodeCameraXWeb;
export { CapacitorQRCodeCameraX };
