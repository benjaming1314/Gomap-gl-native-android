package com.gomap.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;

public class DeviceUtils {

    public static String getAndroidId(Activity activity) {
        return Settings.System.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
}
