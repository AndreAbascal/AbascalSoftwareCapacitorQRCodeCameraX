import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(CapacitorQRCodeCameraX)
public class CapacitorQRCodeCameraX: CAPPlugin {
    
    @objc func start(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            let QRCodeCameraX = QRCodeCameraXViewController();
            QRCodeCameraX.invalid_format_label = call.getString("invalid_format_label")!;
            QRCodeCameraX.open_settings_label = call.getString("open_settings_label")!;
            QRCodeCameraX.permission_again_label = call.getString("permission_again_label")!;
            QRCodeCameraX.permission_again_title = call.getString("permission_again_title")!;
            QRCodeCameraX.qrcode_label = call.getString("qrcode_label")!;
            QRCodeCameraX.url_prefix = call.getString("url_prefix")!;
            QRCodeCameraX.finish = {(status: String, codigo: String) -> () in
                if(status == "OK" || status == "CANCEL"){
                    call.resolve([
                        "status": status,
                        "codigo": codigo
                    ]);
                }else{
                    call.reject("Ocorreu um erro desconhecido.");
                }
            }
            self.bridge.viewController.present(QRCodeCameraX, animated: true, completion: nil);
        }
    }
}
