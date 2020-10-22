package com.covrsecurity.io.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.covrsecurity.io.app.AppAdapter;

public class ScreenUtils {

    public static float getScreenDpRate(Context context) {
        return context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT;
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setupStatusBar(View statusBar, Activity activity){
        int statusBarHeight = getStatusBarHeight(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && statusBarHeight > 0) {
            statusBar.getLayoutParams().height = statusBarHeight;
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static boolean hasNavBar() {
        int id = AppAdapter.resources().getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && AppAdapter.resources().getBoolean(id);
    }
}