import Foundation;

@objc(IonicQRCodeCameraX) class IonicQRCodeCameraX: CDVPlugin {
	@objc(start:)
    func start(_ command: CDVInvokedUrlCommand) {
        let QRCodeCameraX = QRCodeCameraXViewController();
		QRCodeCameraX.invalid_format_label = command.arguments[0] as! String;
		QRCodeCameraX.open_settings_label = command.arguments[1] as! String;
		QRCodeCameraX.permission_again_label = command.arguments[2] as! String;
		QRCodeCameraX.permission_again_title = command.arguments[3] as! String;
		QRCodeCameraX.qrcode_label = command.arguments[4] as! String;
		QRCodeCameraX.url_prefix = command.arguments[5] as! String;
		QRCodeCameraX.finish = {(status: String, codigo: String) -> () in
			let message: String;
            let pluginResult: CDVPluginResult;
			if(status == "OK"){
				message = "{\"status\": \""+status+"\",\"codigo\": \""+codigo+"\"}";
				pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: message);
			}else if(status == "CANCEL"){
				message = "{\"status\": \""+status+"\",\"codigo\": \""+codigo+"\"}";
				pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: message);
			}else{
				message = "{\"erro\": \"Ocorreu um erro desconhecido.\"}";
				pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: message);
			}
			self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
		}
		self.viewController.present(QRCodeCameraX, animated: true, completion: nil);
    }
}