package com.covrsecurity.io.data.utils;

import android.app.Activity;

import com.google.android.gms.common.GoogleApiAvailability;

public class PlayServicesUtils {

    public static int playServicesStatus(Activity activity) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
    }
}
