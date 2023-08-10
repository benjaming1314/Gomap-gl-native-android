package com.gomap.demo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.gomap.demo.GomapApplication;

public class ScreenUtil {

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public static int dp2px(int dp) {
        DisplayMetrics displayMetrics = GomapApplication.getInstance().getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public static int dp2px(double dp) {
        DisplayMetrics displayMetrics = GomapApplication.getInstance().getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        //((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager wm = (WindowManager) GomapApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);

        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

}
