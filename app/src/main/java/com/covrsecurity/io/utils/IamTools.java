package com.covrsecurity.io.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.model.deeplink.AuthorizationDeeplink;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.activity.BaseActivity;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;

public class IamTools {

    public static void reRegistration(Activity activity) {
        Intent intent = new Intent(activity, UnauthorizedActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pIntent);
        clearAllData(activity);
        activity.finish();
    }

    public static void clearAllData(Activity activity) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).stopStandingByUpdates();
        }
        AppAdapter.settings().dropAll();
    }

    public static void openUnautorizedActivity(Activity activity) {
        Context context = AppAdapter.context();
        Intent i = new Intent(context, UnauthorizedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(ConstantsUtils.INTENT_KEY_FOR_TUTORIAL_FRAGMENT, true);
        context.startActivity(i);
        activity.finish();
    }

    public static void startAuthorizedActivity(Activity activity, final boolean isAfterRegistration) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Intent intent = new Intent(AppAdapter.context(), AuthorizedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (isAfterRegistration) {
            intent.putExtra(AuthorizedActivity.KEY_IS_ENTERED_AFTER_REGISTRATION, true);
        }
        AuthorizationDeeplink deeplink = getAuthorizationDeeplink(activity);
        if (deeplink != null && deeplink.isValid()) {
            intent.putExtra(AuthorizedActivity.EXTRA_AUTHORIZATION_DEEPLINK, deeplink);
            activity.getIntent().setData(null);
        }
        AppAdapter.context().startActivity(intent);
        activity.finish();
    }

    private static AuthorizationDeeplink getAuthorizationDeeplink(Activity activity) {
        Intent i = activity.getIntent();
        return Intent.ACTION_VIEW.equals(i.getAction()) && i.getData() != null
                ? new AuthorizationDeeplink(i.getData()) : null;
    }

}
