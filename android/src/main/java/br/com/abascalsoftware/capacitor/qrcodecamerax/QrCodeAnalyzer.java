package br.com.abascalsoftware.capacitor.qrcodecamerax;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;

public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {
    public interface QrCodeListener {
        public void onQrCodeDetected(List<FirebaseVisionBarcode> barcodes);
    }
    private final static String TAG = "QCS->QrCodeAnalyzer";
    private QrCodeListener qrCodeListener;
    @Override
    public void analyze(ImageProxy image, int rotationDegrees) {
        if (image == null || image.getImage() == null) {
            return;
        }
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        int rotation = this.rotationDegreesToFirebaseRotation(rotationDegrees);
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromMediaImage(image.getImage(),rotation);
        detector.detectInImage(visionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                if(barcodes.size() > 0){
                    QrCodeAnalyzer.this.onQrCodesDetected(barcodes);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure detector");
            }
        });
    }

    private void onQrCodesDetected(List<FirebaseVisionBarcode> barcodes){
        Log.d(TAG,"onQrCodesDetected");
        this.qrCodeListener.onQrCodeDetected(barcodes);
    }

    private int rotationDegreesToFirebaseRotation(int rotationDegrees){
        switch(rotationDegrees){
            default:
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
        }
    }

    public void setQrCodeListener(QrCodeListener qrCodeListener){
        this.qrCodeListener = qrCodeListener;
    }
}
