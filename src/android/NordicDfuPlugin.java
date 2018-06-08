package com.byd.wwb.dfu;

import android.app.NotificationManager;
import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import android.os.Handler;

/**
 * Created by wang.wenbin1 on 2017/11/3.
 */

public class NordicDfuPlugin extends CordovaPlugin {
    private Context context;
    private CallbackContext stateChangedCallback = null;
    private CallbackContext progressChangedCallback = null;
    private CallbackContext startDFUCallback = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = cordova.getActivity();
        DfuServiceListenerHelper.registerProgressListener(context, mDfuProgressListener);
    }

    @Override
    public void onDestroy() {
        DfuServiceListenerHelper.unregisterProgressListener(context, mDfuProgressListener);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if("startDFU".equals(action)){
            startDFU(args,callbackContext);
        }
        else if("watchStateChanged".equals(action)){
            watchStateChanged(args,callbackContext);
        }
        else if("watchProgressChanged".equals(action)){
            watchProgressChanged(args,callbackContext);
        }
        else {
            return false;
        }
        return true;
    }

    private void startDFU(JSONArray args,CallbackContext callbackContext){
        startDFUCallback = callbackContext;
        try{
            startDFU(args.getString(0),args.getString(1),args.getString(2));
            // callbackContext.success();
        }catch (Exception e){
            startDFUCallback.error(e.getMessage());
        }
    }

    private void startDFU(String address, String name, String filePath ) {

        final DfuServiceInitiator starter = new DfuServiceInitiator(address)
                .setKeepBond(false);
        if (name != null) {
            starter.setDeviceName(name);
        }
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        starter.setZip(filePath);
        final DfuServiceController controller = starter.start(context, DfuService.class);
    }

    private void watchStateChanged(JSONArray args,CallbackContext callbackContext){
        stateChangedCallback = callbackContext;
    }

    private void watchProgressChanged(JSONArray args,CallbackContext callbackContext){
        progressChangedCallback=callbackContext;
    }

    private void setWatchCallbackNull() {
        if(stateChangedCallback!=null) stateChangedCallback = null;
        if(progressChangedCallback!=null) progressChangedCallback = null;
    }

    private void sendStateUpdate(final String state, final String deviceAddress) {
        if(stateChangedCallback!=null){
            JSONObject json = new JSONObject();
            try {
                json.put("state",state);
                json.put("deviceAddress",deviceAddress);
                PluginResult result = new PluginResult(PluginResult.Status.OK,json);
                result.setKeepCallback(true);
                stateChangedCallback.sendPluginResult(result);
//                stateChangedCallback.success(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
        if(progressChangedCallback!=null){
            JSONObject json = new JSONObject();
            try {
                json.put("deviceAddress",deviceAddress);
                json.put("deviceAddress", deviceAddress);
                json.put("percent", percent);
                json.put("speed", speed);
                json.put("avgSpeed", avgSpeed);
                json.put("currentPart", currentPart);
                json.put("partsTotal", partsTotal);

                PluginResult result = new PluginResult(PluginResult.Status.OK,json);
                result.setKeepCallback(true);
                progressChangedCallback.sendPluginResult(result);
//                progressChangedCallback.success(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            sendStateUpdate("CONNECTING", deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            sendStateUpdate("DFU_PROCESS_STARTING", deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            sendStateUpdate("ENABLING_DFU_MODE", deviceAddress);
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            sendStateUpdate("FIRMWARE_VALIDATING", deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            sendStateUpdate("DEVICE_DISCONNECTING", deviceAddress);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            sendStateUpdate("DFU_COMPLETED", deviceAddress);
            setWatchCallbackNull();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            sendStateUpdate("DFU_ABORTED", deviceAddress);
            setWatchCallbackNull();
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            sendProgressChanged(deviceAddress,percent,speed,avgSpeed,currentPart,partsTotal);
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            sendStateUpdate("DFU_FAILED", deviceAddress);
            setWatchCallbackNull();
            if(startDFUCallback!=null)
            startDFUCallback.error(message);
        }
    };
}
