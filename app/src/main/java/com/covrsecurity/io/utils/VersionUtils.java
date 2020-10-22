package com.covrsecurity.io.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public final class VersionUtils {

    public static String versionCode(Context context) {
        try {
            return Integer.toString(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static String versionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

}
