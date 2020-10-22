package com.covrsecurity.io.app.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.domain.entity.request.NotificationHubRegistrationRequestEntity;
import com.covrsecurity.io.domain.usecase.registred.NotificationHubRegistrationUseCase;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

// TODO !!! change to work manager
public class RegistrationIntentService extends IntentService {

    public static final String REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE";

    private static final String TAG = "RegIntentService";

    @Inject
    NotificationHubRegistrationUseCase hubRegistrationUseCase;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.w(TAG, "Start registration");
            Task<InstanceIdResult> instanceId = FirebaseInstanceId.getInstance().getInstanceId();
            InstanceIdResult result = Tasks.await(instanceId);
            if (result != null) {
                String newPushToken = result.getToken();
                hubRegistrationUseCase.execute(new NotificationHubRegistrationRequestEntity(newPushToken)).blockingAwait();
                Log.w(TAG, "Success to complete registration");
                AppAdapter.settings().setPushToken(newPushToken);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete registration", e);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
