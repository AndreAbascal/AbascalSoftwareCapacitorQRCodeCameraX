declare module "@capacitor/core" {
	interface PluginRegistry {
		CapacitorQRCodeCameraX: CapacitorQRCodeCameraXPlugin;
	}
}

export interface CapacitorQRCodeCameraXPlugin {
	start(params: any): Promise<any>;
}
