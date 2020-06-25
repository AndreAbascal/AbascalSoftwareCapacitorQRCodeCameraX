//
//  QRCodeCameraXViewController.swift
//  QRCodeCameraX
//
//  Created by Abascal Software on 06/02/20.
//  Copyright © 2020 Abascal Software. All rights reserved.
//

import UIKit
import AVFoundation

class QRCodeCameraXViewController: UIViewController, AVCaptureMetadataOutputObjectsDelegate {
    var authorized: Bool = false
    var captureSession: AVCaptureSession!
    var invalid_format_label: String = ""
    var open_settings_label: String = ""
    var permission_again_label: String = ""
    var permission_again_title: String = ""
    var qrcode_label: String = "";
    var url_prefix: String = ""
    var video = AVCaptureVideoPreviewLayer()
    
    public var finish: ((String, String) -> ())?;
    
    @objc func cancel(sender: UIButton) {
        dismiss(animated: true) {
            self.finish?("CANCEL","");
        }
    }
    
    func heightForView(text:String, font:UIFont, width:CGFloat) -> CGFloat{
        let label:UILabel = UILabel(frame: CGRect(x: 0, y: 0, width: width, height: CGFloat.greatestFiniteMagnitude))
        label.numberOfLines = 0
        label.lineBreakMode = NSLineBreakMode.byWordWrapping
        label.font = font
        label.text = text

        label.sizeToFit()
        return label.frame.height
    }
    
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if (captureSession?.isRunning == true) {
            captureSession.stopRunning()
        }

        if let metadataObject = metadataObjects.first {
            guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject else { return }
            guard let stringValue = readableObject.stringValue else { return }
            if(stringValue.starts(with: self.url_prefix)){
                let code: String = stringValue.replacingOccurrences(of: self.url_prefix, with: "");
                ok(code: code)
            }
        }

    }
    
    func ok(code: String){
        dismiss(animated: true) {
            self.finish!("OK",code);
        }
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    func requestPermissionManually(){
        let alert = UIAlertController(title: permission_again_title, message: permission_again_label, preferredStyle: .alert)
        let settingsAction = UIAlertAction(title: open_settings_label, style: .default, handler: {action in
            // open the app permission in Settings app
            UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!, options: [:], completionHandler: nil)
        })
        let cancelAction = UIAlertAction(title: "Cancelar", style: .default, handler: { action in
            self.dismiss(animated: true) {
                self.finish?("CANCEL","");
            }
        })
        alert.addAction(settingsAction)
        alert.addAction(cancelAction)
        alert.preferredAction = settingsAction
        self.present(alert, animated: true, completion: nil)
    }
    @objc func reRunPermissionFlow(){
        self.runPermissionFlow()
    }
    func runPermissionFlow(){
        switch AVCaptureDevice.authorizationStatus(for: .video) {
            case .authorized: // The user has previously granted access to the camera.
                self.authorized = true
                self.setupCaptureSession()
                self.showViews()
            case .notDetermined: // The user has not yet been asked for camera access.
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    if granted {
                        self.authorized = true
                        self.setupCaptureSession()
                        self.showViews()
                    }
                }
            case .denied: // The user has previously denied access.
                self.authorized = false
                requestPermissionManually()
            case .restricted:
                self.authorized = false
                requestPermissionManually()
            @unknown default:
                self.authorized = false
                requestPermissionManually()
        }
    }
    
    func setHeader(){
        DispatchQueue.main.async {
            let headerHeight: CGFloat = 64;
            let header = UIView();
            header.translatesAutoresizingMaskIntoConstraints = false;
            let backButton = UIButton()
            backButton.setTitle("Voltar", for: .normal);
            backButton.setTitleColor(UIColor.white, for: UIControl.State.normal);
            backButton.frame = CGRect(x: 0, y: 0, width: 120, height: Int(80));
            backButton.addTarget(self, action: #selector(self.cancel(sender:)), for: UIControl.Event.touchUpInside);
            header.addSubview(backButton)
            self.view.addSubview(header);
            NSLayoutConstraint(item: header, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: headerHeight).isActive = true;
            NSLayoutConstraint(item: header, attribute: .leading, relatedBy: .equal, toItem: self.view, attribute: .leading, multiplier: 1.0, constant: 0).isActive = true;
            NSLayoutConstraint(item: header, attribute: .trailing, relatedBy: .equal, toItem: self.view, attribute: .trailing, multiplier: 1.0, constant: 0).isActive = true;
            NSLayoutConstraint(item: header, attribute: .top, relatedBy: .equal, toItem: self.view, attribute: .top, multiplier: 1.0, constant: 0).isActive = true;
        }
    }
    
    func setupCaptureSession(){
        let session = AVCaptureSession()
        let captureDevice = AVCaptureDevice.default(for: AVMediaType.video)
        do {
            let input = try AVCaptureDeviceInput(device: captureDevice!)
            session.addInput(input)
        }catch{
            dismiss(animated: true) {
                
            }
        }
        let output = AVCaptureMetadataOutput()
        session.addOutput(output)
        output.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
        output.metadataObjectTypes = [.qr]
        video = AVCaptureVideoPreviewLayer(session: session)
        video.frame = view.layer.bounds
        view.layer.addSublayer(video)
        session.startRunning()
    }
    
    func showViews(){
        setHeader()
        let screenHeight = view.frame.size.height
        let qrCodeHeight: CGFloat = 48
        let tarjaHeight: CGFloat = 72
        let espacamento: CGFloat = (tarjaHeight-qrCodeHeight)/2;
        let tarja = UIView()
        tarja.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 191/255)
        tarja.frame = CGRect(x: 0, y: (screenHeight-tarjaHeight) , width: self.view.frame.width, height: tarjaHeight)
        let qrCode = UIImageView(frame: CGRect(x: espacamento, y: espacamento, width: qrCodeHeight, height: qrCodeHeight))
        qrCode.image = UIImage(named: "QRCode")
        tarja.addSubview(qrCode)
        let lwX: CGFloat = (espacamento+qrCodeHeight+espacamento)
        let lwY: CGFloat = espacamento
        let lwHeight: CGFloat = (tarjaHeight - espacamento*2)
        let lwWidth: CGFloat = (self.view.frame.width - lwX - espacamento)
        let labelWrapper = UIView()
//        labelWrapper.backgroundColor = UIColor(red: 1, green: 0, blue: 0, alpha: 1)
        labelWrapper.frame = CGRect(x: lwX, y: lwY , width: lwWidth, height: lwHeight)
        let font = UIFont.preferredFont(forTextStyle: .body);
        let label = UILabel()
        label.font = font
        label.frame = CGRect(x: 0, y: 0, width: lwWidth, height: 21)
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        label.textColor = UIColor.white;
        label.text = qrcode_label;
        labelWrapper.addSubview(label)
        tarja.addSubview(labelWrapper)
        self.view.addSubview(tarja)
    }

    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .portrait
    }
    
    override func viewDidAppear(_ animated: Bool){
        super.viewDidAppear(animated)
        NotificationCenter.default.addObserver(self, selector: #selector(self.reRunPermissionFlow), name: UIApplication.willEnterForegroundNotification, object: UIApplication.shared)
        self.runPermissionFlow()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        NotificationCenter.default.removeObserver(self)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if (captureSession?.isRunning == false) {
            captureSession.startRunning()
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if (captureSession?.isRunning == true) {
            captureSession.stopRunning()
        }
    }

}

