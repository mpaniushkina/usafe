package com.covrsecurity.io.utils;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.app.IamApp;
import com.google.gson.Gson;

import timber.log.Timber;

public class LogUtil {

    private static final String APP_TAG = "[" + IamApp.class.getName() + "]";
    private static final boolean useGlobalLogs = false;
    private static final Gson GSON = new Gson();

    public static Gson getGson() {
        return GSON;
    }

    public static void initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static void printError(String message) {
        Log.e(APP_TAG, message);
        if (useGlobalLogs) {
            System.err.print(APP_TAG + ": " + message);
        }
    }

    public static void printError(String... messages) {
        if (messages != null) {
            printError(TextUtils.join(", ", messages));
        }
    }

    public static void printJson(@NonNull String message, Number number) {
        Timber.d(message, number);
    }

    public static void printJson(@NonNull String message, Object o) {
        Timber.d(message, GSON.toJson(o));
    }

    public static void printJsonNamed(@NonNull String message, String name, Object o) {
        Timber.d(message, name, GSON.toJson(o));
    }
}
