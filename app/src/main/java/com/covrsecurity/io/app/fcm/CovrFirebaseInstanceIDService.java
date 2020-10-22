package com.covrsecurity.io.app.fcm;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import timber.log.Timber;

/**
 * Created by abaranov on 15.6.16.
 */
public class CovrFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String[] TOPICS = {"global"};
    public static final String REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE";

    @Override
    public void onNewToken(@NonNull String token) {
        // Get updated InstanceID token.
        Timber.d("FCM token: %s", token);

        subscribeToTopics();

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void subscribeToTopics() {
        for (String topic : TOPICS) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            Timber.d("Subscribed to %s topic", topic);
        }
    }
}

