package com.covrsecurity.io.utils;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import timber.log.Timber;

public class PlayServicesUtils {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkPlayServicesAndShowErrorDialog(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.e("This device does not support google play services.");
            }
            return false;
        }
        return true;
    }

    public static int playServicesStatus(Activity activity) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
    }

    public static void showPlayServicesErrorDialog(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
    }
}
