package com.covrsecurity.io.utils;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.safetynet.HarmfulAppsData;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;

import java.util.List;

public class DeviceIntegrityHelper {

    public static void checkAndWarnForMaliciousApps(AppCompatActivity activity) {

        SafetyNetClient safetyNetClient = SafetyNet.getClient(activity);

        safetyNetClient.enableVerifyApps().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                if (result != null && result.isVerifyAppsEnabled()) {
                    listHarmfulApps(activity);
                }
            }
        });
    }

    private static void listHarmfulApps(AppCompatActivity activity) {
        SafetyNetClient safetyNetClient = SafetyNet.getClient(activity);

        safetyNetClient.listHarmfulApps()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    SafetyNetApi.HarmfulAppsResponse result = task.getResult();

                    List<HarmfulAppsData> appList = null;

                    if (result != null) {
                        appList = result.getHarmfulAppsList();
                    }

                    if (appList != null && !appList.isEmpty()) {

                        final PackageManager packageManager = activity.getApplication()
                                .getPackageManager();

                        String[] packageNames = new String[appList.size()];
                        String[] appNames = new String[appList.size()];
                        int i = 0;
                        for (HarmfulAppsData harmfulApp : appList) {
                            try {
                                ApplicationInfo applicationInfo =
                                        packageManager.getApplicationInfo(
                                                harmfulApp.apkPackageName, 0);
                                packageNames[i++] = harmfulApp.apkPackageName != null ?
                                        harmfulApp.apkPackageName: "";
                                appNames[i++] = (String) (applicationInfo != null ?
                                        packageManager.getApplicationLabel(applicationInfo) :
                                        "No Name");
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        DialogUtils.showHarmfulAppsDialogFragment(activity,
                                appNames, (view) -> {}, (parent, view, position, id) ->
                                        uninstallApp(activity, packageNames[position]));
                    }
                }
            });
    }

    private static void uninstallApp(AppCompatActivity compatActivity,String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        compatActivity.startActivity(intent);
    }
}
