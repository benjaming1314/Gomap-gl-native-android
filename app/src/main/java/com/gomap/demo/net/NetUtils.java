package com.gomap.demo.net;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import com.gomap.sdk.geometry.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetUtils {

    private Call call ;

    private NetUtils(){}

    static class NetUtilsHolder{
        static NetUtils instance = new NetUtils();
    }

    public static NetUtils getInstance(){
        return NetUtils.NetUtilsHolder.instance;
    }

    @SuppressLint("LongLogTag")
    public synchronized void requestPoi(LatLng center ,Activity activity, NetCallBack callBack) {

        if (call != null){
            call.cancel();
        }

//        {"location":{"lng":"54.123456","lat":"24.123456"},"imei":"xxxx","center":{"lng":"54.123456","lat":"24.123456"},"radius":15000}
        String url = "https://gomap-dev.kharita.ai/api-server/api/poi/near-by/search";

        HashMap map = new HashMap<String,Object>();
        map.put("imei", Settings.System.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
        map.put("radius",15000);

        HashMap location = new HashMap<String,Object>();
        location.put("lng",center.getLongitude());
        location.put("lat",center.getLatitude());
        map.put("location",location);

        HashMap centerMap = new HashMap<String,Object>();
        centerMap.put("lng",center.getLongitude());
        centerMap.put("lat",center.getLatitude());
        map.put("center",centerMap);

        String requestStr = new Gson().toJson(map);

        Log.i("NetUtils",requestStr);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), requestStr);

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("NetUtils onFailure", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                callBack.onCallBack(result);
                Log.i("NetUtils result", result);

            }
        });

    }

    public interface NetCallBack {
        public void onCallBack(String response);
    }

}
