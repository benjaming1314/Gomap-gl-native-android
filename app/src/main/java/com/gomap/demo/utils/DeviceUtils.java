package com.gomap.demo.utils;

import android.app.Activity;
import android.provider.Settings;

public class DeviceUtils {

    public static String getAndroidId(Activity activity) {
        return Settings.System.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
