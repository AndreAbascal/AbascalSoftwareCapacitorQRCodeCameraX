package br.com.abascalsoftware.capacitor.qrcodecamerax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
//import androidx.camera.core.PreviewConfig;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.util.List;

public class ScannerBarcodeActivity extends AppCompatActivity {

    public static final int OPEN_APP_SETTINGS = 12345;
    private static int REQUEST_CODE_CAMERA = 38;
    private static final String TAG = "IQCX->ScannerActivity";

    private String currentView = "";
    private String invalid_format_label = "";
    private String permission_again_label = "";
    final String[] permissions = new String[] {Manifest.permission.CAMERA};
    private Preview mPreview;
    private String prefix = "";
    private TextureView textureView;

    public void askForPermissions(){
        ActivityCompat.requestPermissions(this,this.permissions,REQUEST_CODE_CAMERA);
    }

    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"ONCREATE!");
        super.onCreate(savedInstanceState);
        final int layoutScannerActivity = this.getResources().getIdentifier("scanner_activity", "layout", this.getPackageName());
        setContentView(layoutScannerActivity);
        this.currentView = "scanner_activity";
        final int idTextureView = this.getResources().getIdentifier("texture_view", "id", this.getPackageName());
        textureView = findViewById(idTextureView);
        //PEDIR PERMISSAO CAMERA
        if(isCameraPermissionGranted()){
            textureView.post(new Runnable() {
                @Override
                public void run() {
                    ScannerBarcodeActivity.this.startCamera();
                }
            });
        }else{
            this.askForPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_CAMERA){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG,"onRequestPermissionsResult GRANTED");
                textureView.post(new Runnable() {
                    @Override
                    public void run() {
                        ScannerBarcodeActivity.this.startCamera();
                    }
                });
            }else{
                Log.d(TAG,"onRequestPermissionsResult DENIED");
                if(this.currentView != "permission_denied"){
                    final int layoutPermissionDenied = this.getResources().getIdentifier("permission_denied", "layout", this.getPackageName());
                    setContentView(layoutPermissionDenied);
                    this.currentView = "permission_denied";
                };
                View.OnClickListener listener;
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0]);
                if(showRationale){
                    Log.d(TAG,"showRationale = true");
                    listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ScannerBarcodeActivity.this.askForPermissions();
                        }
                    };
                }else{
                    Log.d(TAG,"showRationale = false");

                    listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            ScannerBarcodeActivity.this.openAPPSettings();
                            new AlertDialog.Builder(ScannerBarcodeActivity.this)
                                .setMessage(ScannerBarcodeActivity.this.permission_again_label)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ScannerBarcodeActivity.this.openAPPSettings();
                                    }
                                }).setTitle("Permissão necessária")
                                .setCancelable(true)
                                .create()
                                .show();
                        }
                    };
                }
                final int idPermissionAgain = this.getResources().getIdentifier("request_permission_again", "id", this.getPackageName());
                findViewById(idPermissionAgain).setOnClickListener(listener);
            }
        }else{
            Log.d(TAG,"onRequestPermissionsResult desconhecido");
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"ONRESUME!");
        super.onResume();
        Log.d(TAG,"this.currentView: "+this.currentView);
        if(this.currentView == "permission_denied"){
            if(this.isCameraPermissionGranted()){
                Log.d(TAG,"GRANTED!");
                final int layoutScannerActivity = this.getResources().getIdentifier("scanner_activity", "layout", this.getPackageName());
                setContentView(layoutScannerActivity);
                final int idTextureView = this.getResources().getIdentifier("texture_view", "id", this.getPackageName());
                textureView = findViewById(idTextureView);
                Log.d(TAG,"GRANTED 01!");
                textureView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"VOU ABRIR A CAMERA!");
                        ScannerBarcodeActivity.this.startCamera();
                    }
                });
            }else{
                Log.d(TAG,"NOT GRANTED!");
            }
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart!");
        Bundle extras = getIntent().getExtras();
        Log.d(TAG,"onStart 02");
        if(extras != null){
            Log.d(TAG,"extras NOT NULL");
            for (String key : extras.keySet()) {
                Log.d(TAG,"EXTRA KEY: "+key.toString());
                Object value = extras.get(key);
                Log.d(TAG,"EXTRA VALUE: "+value.toString());
                switch(key){
                    case "invalid_format_label":
                        this.setInvalidLabel(value.toString());
                        break;
                    case "permission_again_label":
                        this.setPermissionLabel(value.toString());
                        break;
                    case "url_prefix":
                        this.setPrefix(value.toString());
                    default:
                        break;
                }
            }
        }
        Log.d(TAG,"onStart 03");
        super.onStart();
        Log.d(TAG,"onStart 04");
    }

    private void onToggleTorch(boolean value){
        mPreview.enableTorch(value);
    }

    private void openAPPSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void setInvalidLabel(String label){
        this.invalid_format_label = label;
    }

    private void setPermissionLabel(String label){
        this.permission_again_label = label;
    }

    private void setPrefix(String prefix){
		Log.d(TAG,"PREFIX ANTES: "+this.prefix);
		this.prefix = prefix;
		Log.d(TAG,"PREFIX DEPOIS: "+this.prefix);
    }

    private void startCamera(){
        try {
            PreviewConfig previewConfig = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.BACK).build();
            Preview preview = new Preview(previewConfig);
            mPreview = preview;
            preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener(){
                @Override
                public void onUpdated(Preview.PreviewOutput previewOutput) {
                    ViewGroup parent = (ViewGroup) textureView.getParent();
                    parent.removeView(textureView);
                    parent.addView(textureView, 0);
                    textureView.setSurfaceTexture(previewOutput.getSurfaceTexture());
                }
            });
            ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder().build();
            ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
            QrCodeAnalyzer qrCodeAnalyzer = new QrCodeAnalyzer();
            qrCodeAnalyzer.setQrCodeListener(new QrCodeAnalyzer.QrCodeListener() {
                @Override
                public void onQrCodeDetected(List<FirebaseVisionBarcode> barcodes) {
                    Log.d(TAG,"CHEGUEI NO LISTENER DE BARCODES COM "+Integer.toString(barcodes.size())+" BARCODES");
                    for(FirebaseVisionBarcode barcode: barcodes){
                        String maquinaURL = barcode.getRawValue();
                        Log.d(TAG,"URL DA MAQUINA: "+maquinaURL);
                        String prefix = ScannerBarcodeActivity.this.prefix;
                        Log.d(TAG,"PREFIX: "+prefix);
                        if(maquinaURL.startsWith(prefix)){
                            String maquinaCodigo = maquinaURL.replace(prefix,"");
                            Log.d(TAG,"CODIGO DA MAQUINA: "+maquinaCodigo);
                            Intent intent = new Intent();
                            intent.putExtra("status", "OK");
                            intent.putExtra("codigo",maquinaCodigo);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            });
            imageAnalysis.setAnalyzer(qrCodeAnalyzer);
            CameraX.bindToLifecycle((LifecycleOwner) this, preview, imageAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
