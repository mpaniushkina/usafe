package com.covrsecurity.io.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import android.widget.Toast;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.model.Push;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.fragment.authorized.StandingByFragment;

/**
 * Created by alex on 7.5.16.
 */
public final class NotificationUtils {

    private static final int DEFAULT_NOTIFICATION_ID = 32 << 4;
    private static final long DEFAULT_NOTIFICATION_VIBRATE_TIME = 800;
    private static final String REQUESTS_LIST_FRAGMENT_NAME = StandingByFragment.class.getName();
    private static final String CHANNEL_ID = BuildConfig.APPLICATION_ID + ".pendingRequests";
    private static final String CHANNEL_SILENT_ID = BuildConfig.APPLICATION_ID + ".badgeCount";

    public static void vibrateDefault(final Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            if (AppAdapter.settings().getPushVibrateEnable()) {
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (v.hasVibrator()) {
                    v.vibrate(DEFAULT_NOTIFICATION_VIBRATE_TIME);
                }
            }
        }
    }

    public static void playNotificationSoundDefault(final Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            if (AppAdapter.settings().getPushSoundEnable()) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, soundUri);
                try {
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void buildNotification(final Context context, final Push push) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(AppAdapter.resources().getString(R.string.app_name))
                    .setVibrate(new long[0])
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentText(push.getAlert())
                    .setTicker(push.getAlert());

            Intent resultIntent = new Intent(context, UnauthorizedActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(UnauthorizedActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(context);
            }
            notificationManager.notify(DEFAULT_NOTIFICATION_ID, mBuilder.build());
        }
    }

    public static void buildSilentNotification(final Context context, final Push push) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_SILENT_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setContentTitle(AppAdapter.resources().getString(R.string.app_name))
                    .setVibrate(new long[0])
                    .setContentText(push.getAlert())
                    .setTicker(push.getAlert());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createSilentNotificationChannel(context);
            }
            notificationManager.notify(DEFAULT_NOTIFICATION_ID, mBuilder.build());
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(final Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            String name = context.getString(R.string.channel_name);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.channel_description));
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(false);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static void createSilentNotificationChannel(final Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            String name = context.getString(R.string.channel_name);
            NotificationChannel channel = new NotificationChannel(CHANNEL_SILENT_ID, name,
                    NotificationManager.IMPORTANCE_MIN);
            channel.setDescription(context.getString(R.string.channel_silent_description));
            channel.enableLights(false);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void makeInAppNotification(Context context) {
        playInappNotificationSound(context);
        playInappNotificationVibration(context);
        if (!REQUESTS_LIST_FRAGMENT_NAME.equals(ActivityUtils.getLastFragmentName())) {
            ActivityUtils.scheduleOnMainThread(() -> {
                        Toast toast = Toast.makeText(context, context.getString(R.string.toast_inapp_new_request), Toast.LENGTH_SHORT);
                        toast.show();
                    }
            );
        }
    }

    private static void playInappNotificationSound(Context context) {
        if (AppAdapter.settings().getInappIncomingSoundEnabled()) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, soundUri);
            try {
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void playInappNotificationVibration(Context context) {
        if (AppAdapter.settings().getInappIncomingVibrationEnabled()) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v.hasVibrator()) {
                v.vibrate(DEFAULT_NOTIFICATION_VIBRATE_TIME);
            }
        }
    }

    public static void clearNotification(final Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
    }

}
