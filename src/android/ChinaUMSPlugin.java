package com.ttebd.chinaums;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.ttebd.chinaums.ChinaUMSPrinter;
import com.ums.AppHelper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class ChinaUMSPlugin extends CordovaPlugin {
    public static final String TAG = "chinaPlugin";
    private CallbackContext _callbackContext;
    private Activity activity;
    private PluginResult pluginResult;
    private String appName = "";
    private String transId = "";
    private String invokeType = "";
    private JSONArray printDataArray = null;
    private JSONObject transData = null;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        _callbackContext = callbackContext;
        activity = this.cordova.getActivity();
        this.cordova.setActivityResultCallback(ChinaUMSPlugin.this);

        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if (action.equals("chinaUMS")) {

            JSONObject params = args.getJSONObject(0);
            System.out.println("=====银联商务-params=====:" + params);


            appName = params.optString("appName");
            transId = params.optString("transId");
            invokeType = params.optString("invokeType");
            transData = params.optJSONObject("transData");
            printDataArray = params.optJSONArray("printDataArray");


            Runnable r = new Runnable() {
                @Override
                public void run() {


                    switch (invokeType) {
                        case "callTrans":
                            AppHelper.callTrans(activity, appName, transId, transData);
                            break;
                        case "callPrint":

                            ChinaUMSPrinter chinaUMSPrinter = new ChinaUMSPrinter(activity, callbackContext,printDataArray);
                            chinaUMSPrinter.deviceServiceLogin();

//              打印图片

/*              JSONObject imgObj = obj.getJSONObject(key);

              String printData = imgObj.optString("printData");

              InputStream in = null;
//
              File file;
              try {
                file = new File(Environment.getExternalStorageDirectory(), printData);
                in = new FileInputStream(file);
                Bitmap imageToPrint = BitmapFactory.decodeStream(in);
                builder.appendBitmap(imageToPrint, false, width, bothScale);
              } catch (FileNotFoundException e) {
                e.printStackTrace();
              }*/



//              创建二维码代码
/*
              JSONObject qrCodeObj = obj.getJSONObject(key);

              String printData = qrCodeObj.optString("printData");

              TestQrcode testQrcode = new TestQrcode();

              Bitmap bitmap = null;
              try {
                bitmap = testQrcode.createQRCode(printData, 450);
              } catch (WriterException e) {
                e.printStackTrace();
              }*/


                            break;
                        default:
                            _callbackContext.error("没有找到此交易类型");
                            break;
                    }

//            String data = "{\"amt\":\"" + "1" + "\"}";
//            JSONObject json = new JSONObject(data);
//        AppHelper.callTrans(cordova.getActivity(), "银行卡收款", "消费", json);
//            AppHelper.callTrans(cordova.getActivity(), "银行卡收款", "打印交易汇总", json);
//            AppHelper.callTrans(cordova.getActivity(), "银行卡收款", "余额查询", json);

//            AppHelper.callPrint(cordova.getActivity(), "kskjdfsljd\n你好\n123456789124680qwetuioasfghjkxcbmcb\n");

                }
            };

            this.cordova.getThreadPool().execute(r);


//      String message = args.getString(0);
//      this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AppHelper.TRANS_REQUEST_CODE == requestCode) {

//      Log.d(TAG, "resultCode" + resultCode);

            if (Activity.RESULT_OK == resultCode) {
                if (null != data) {
                    StringBuilder result = new StringBuilder();
                    Map<String, String> map = AppHelper.filterTransResult(data);
                    System.out.println("map======" + map);
                    result.append("{\"" + AppHelper.TRANS_APP_NAME + "\":\"" + map.get(AppHelper.TRANS_APP_NAME) + "\",");
                    result.append("\"" + AppHelper.TRANS_BIZ_ID + "\":\"" + map.get(AppHelper.TRANS_BIZ_ID) + "\",");
                    result.append("\"" + AppHelper.RESULT_CODE + "\":\"" + map.get(AppHelper.RESULT_CODE) + "\",");
                    result.append("\"" + AppHelper.RESULT_MSG + "\":\"" + map.get(AppHelper.RESULT_MSG) + "\",");
                    result.append("\"" + AppHelper.TRANS_DATA + "\":" + map.get(AppHelper.TRANS_DATA) + "}");

                    System.out.println("=====银联商务-result=====:" + result);

                    if (null != result) {
                        _callbackContext.success(result + "");
                    }
                } else {
                    formatCallbackJson("06", "intent is null");
                }
            } else {
                formatCallbackJson("07", "resultCode is not RESULT_OK");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public JSONObject formatCallbackJson(String rc, String repText) {
        JSONObject errObj = new JSONObject();
        try {
            errObj.put("rc", rc);
            errObj.put("repText", repText);
        } catch (JSONException e) {
            Log.e(TAG, "onActivityResult:" + errObj);
        }
        return errObj;
    }



}
