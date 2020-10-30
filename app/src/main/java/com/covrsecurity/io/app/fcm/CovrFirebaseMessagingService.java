package com.covrsecurity.io.app.fcm;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.model.Fields;
import com.covrsecurity.io.model.Push;
import com.covrsecurity.io.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;
import timber.log.Timber;

/**
 * Created by abaranov on 15.6.16.
 */
public class CovrFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = CovrFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Timber.d("onMessageReceived() message: getFrom = %s", remoteMessage.getFrom() + ", getData = " +
                remoteMessage.getData() + ", getMessageType=  " + remoteMessage.getMessageType());
        Map<String, String> data = remoteMessage.getData();
        String alert = data.get(Fields.MESSAGE);
        Integer badge = Integer.valueOf(data.get(Fields.PENDING_REQUESTS));
        AppAdapter.settings().savePendingRequest(data.get(Fields.EXPIRATIONS));
        Push push = new Push(alert, badge);
        AppAdapter.settings().setCurrentBadgeCount(badge + AppAdapter.settings().getHistoryBadge());
        ShortcutBadger.applyCount(this, push.getBadge() +
                AppAdapter.settings().getHistoryBadge());
        if (AppAdapter.settings().getPushNotificationsEnable()) {
            if (!IamApp.getInstance().isAppInForeground()) {
                NotificationUtils.vibrateDefault(this);
                NotificationUtils.playNotificationSoundDefault(this);
                NotificationUtils.buildNotification(this, push);
            } else {
                NotificationUtils.makeInAppNotification(this);
            }
        }
        AppAdapter.bus().post(push);
        Analytics.logEvent(this, Analytics.EVENT_REQUEST_INCOMING);
    }
}
