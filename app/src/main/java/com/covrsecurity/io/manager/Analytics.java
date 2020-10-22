package com.covrsecurity.io.manager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.model.error.APIError;

/**
 * Created by abaranov on 15.6.16.
 */
public class Analytics {

    public static void logEvent(Context context, String eventName) {
        //if (! BuildConfig.DEBUG) {
        if (AppAdapter.settings().getDataCollectionEnable()) {
            Log.d("Analytics", "Event: " + eventName);
            FirebaseAnalytics.getInstance(context).logEvent(eventName, null);
        }
    }

    public static void logEvent(Context context, String eventName, APIError error) {
        if (AppAdapter.settings().getDataCollectionEnable()) {
            Log.d("Analytics", "Event: " + eventName);
            if (error != null) {
                Bundle b = new Bundle();
                b.putString(TAG_ERROR_CODE, String.valueOf(error.getErrorCode()));
                if (error.getStatusDescription() != null) {
                    b.putString(TAG_STATUS_DESCRIPTION, error.getStatusDescription());
                }
                FirebaseAnalytics.getInstance(context).logEvent(eventName, b);
            } else {
                FirebaseAnalytics.getInstance(context).logEvent(eventName, null);
            }
        }
    }

    public static void logEvent(Context context, String eventName, Bundle params) {
        //if (! BuildConfig.DEBUG) {
        if (AppAdapter.settings().getDataCollectionEnable()) {
            Log.d("Analytics", "Event: " + eventName);
            FirebaseAnalytics.getInstance(context).logEvent(eventName, params);
        }
    }

    public static final String TAG_ERROR_CODE = "error_code";
    public static final String TAG_STATUS_DESCRIPTION = "status_description";

    public static final String EVENT_APP_LAUNCH = "app_launch";
    public static final String EVENT_SETUP_START = "setup_start";
    public static final String EVENT_SETUP_CANCEL ="setup_cancel";
    public static final String EVENT_RECOVERY_START = "recovery_start";
    public static final String EVENT_PHONE_SUCCESS = "phone_success";
    public static final String EVENT_PHONE_FAILURE = "phone_failure";
    public static final String EVENT_PHONE_REENTER = "phone_reenter";
    public static final String EVENT_PIN_FAILURE = "pin_failure";
    public static final String EVENT_PIN_ERROR = "pin_error";
    public static final String EVENT_PIN_SUCCESS = "pin_success";
    public static final String EVENT_CODE_CREATE = "code_create";
    public static final String EVENT_CODE_CHANGE = "code_change";
    public static final String EVENT_CODE_CHANGE_ATTEMPTS_EXCEEDED = "code_change_attempts_exceeded";
    public static final String EVENT_ADD_PARTNERSHIP_SUCCESS = "add_partnership_success";
    public static final String EVENT_ADD_PARTNERSHIP_FAILURE = "add_partnership_failure";
    public static final String EVENT_REQUEST_INCOMING = "request_incoming";
    public static final String EVENT_REQUEST_TIMEOUT = "request_timeout";
    public static final String EVENT_REQUEST_DENY = "request_deny";
    public static final String EVENT_REQUEST_VERIFY = "request_verify";
    public static final String EVENT_HISTORY_CLEAN_ALL = "history_clean_all";
    public static final String EVENT_HISTORY_CLEAN_TIMED_OUT = "history_clean_timed_out";
    public static final String EVENT_HISTORY_DELETE = "history_selective_removal";
    public static final String EVENT_SETTINGS_UPDATE = "settings_update";
    public static final String EVENT_COMPOSE_EMAIL = "compose_email";
}
