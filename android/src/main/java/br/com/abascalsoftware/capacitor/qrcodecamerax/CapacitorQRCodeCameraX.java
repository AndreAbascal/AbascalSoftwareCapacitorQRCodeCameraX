package br.com.abascalsoftware.capacitor.qrcodecamerax;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@NativePlugin(
	permissions = {

	},
	requestCodes = {
		CapacitorQRCodeCameraX.INTENT_START
	}
)
public class CapacitorQRCodeCameraX extends Plugin {
    public static final int INTENT_START = 1984;
	private static final String TAG = "CQCX->CapQRCodeCameraX";

	@PluginMethod()
    public void start(PluginCall call) {
		saveCall(call);
        Context context = getContext();
		//Log.d(TAG,"CapacitorQRCodeCameraX execute... context.getPackageName(): "+context.getPackageName());
		String invalid_format_label = call.getString("invalid_format_label");
		String open_settings_label = call.getString("open_settings_label");
		String permission_again_label = call.getString("permission_again_label");
		String permission_again_title = call.getString("permission_again_title");
		String qrcode_label = call.getString("qrcode_label");
		String url_prefix = call.getString("url_prefix");
		Intent intent = new Intent(context, ScannerBarcodeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("action", CapacitorQRCodeCameraX.INTENT_START);
		intent.putExtra("invalid_format_label", invalid_format_label);
		intent.putExtra("open_settings_label", open_settings_label);
		intent.putExtra("permission_again_label", permission_again_label);
		intent.putExtra("permission_again_title", permission_again_title);
		intent.putExtra("qrcode_label", qrcode_label);
		intent.putExtra("url_prefix", url_prefix);
		// cordova.setActivityResultCallback (this);
		// cordova.startActivityForResult((CordovaPlugin) this, intent, CapacitorQRCodeCameraX.INTENT_START);
		// startActivity(new Intent(cordova.getActivity(),ScannerBarcodeActivity.class));
		startActivityForResult(call, intent, INTENT_START);
	}
	
	@Override
	public void handleOnStop() {
		Log.d(TAG,"handleOnStop");
		super.handleOnStop();
	}

    @Override
  	protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
		super.handleOnActivityResult(requestCode, resultCode, data);
		Log.d(TAG,"handleOnActivityResult 01");
		PluginCall savedCall = getSavedCall();
		Log.d(TAG,"handleOnActivityResult 02");
		if (savedCall == null) {
			Log.d(TAG,"handleOnActivityResult savedCall is null.");
			return;
		}
		Log.d(TAG,"handleOnActivityResult 03");
        Log.d(TAG,"handleOnActivityResult requestCode: "+Integer.toString(requestCode));
        Log.d(TAG,"handleOnActivityResult resultCode: "+Integer.toString(resultCode));
        switch(requestCode){
			case CapacitorQRCodeCameraX.INTENT_START:
				Log.d(TAG,"handleOnActivityResult resultCode IS INTENT_START");
                if(resultCode == Activity.RESULT_OK){
                    try {
                        Bundle extras = new Bundle();
                        if(data != null){
                            extras = data.getExtras();
                        }
                        JSObject obj = new JSObject();
                        obj.put("status", extras.getString("status"));
                        obj.put("codigo", extras.getString("codigo"));
                        savedCall.resolve(obj);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
			default:
				Log.d(TAG,"handleOnActivityResult OUTRO REQUEST CODE");
                savedCall.reject("ERRO DESCONHECIDO");
        }
    }
}