package com.covrsecurity.io.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;


public class ActivityUtils {
    private static String lastFragmentName = "";

    public static void scheduleOnMainThread(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }

    public static void scheduleOnMainThread(Runnable r, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(r, delay);
    }

    public static void runOnMainThread(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            scheduleOnMainThread(r);
        }
    }

    public static boolean isActivityRunning(final Context context,
                                            final String activityClassName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> activities = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).baseActivity.getClassName().equals(
                    activityClassName)) {
                return true;
            }
        }
        return false;
    }

    public static String getLastFragmentName() {
        return lastFragmentName;
    }

    public static void setLastFragmentName(String fragmentName) {
        lastFragmentName = fragmentName;
    }
}
