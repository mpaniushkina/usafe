package com.covrsecurity.io.data.repository;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;

import com.covrsecurity.io.data.utils.PlayServicesUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.safetynet.SafetyNet;
import com.covrsecurity.io.domain.repository.PlayServicesRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class PlayServicesRepositoryImpl implements PlayServicesRepository {

    private static final String SAFETY_NET_API_KEY1 = "AIzaSyDyNIUs0";
    private static final String SAFETY_NET_API_KEY2 = "-9ihqttBelNUK";
    private static final String SAFETY_NET_API_KEY3 = "8vAPEv89SpACo";

    private final Context context;

    @Inject
    public PlayServicesRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public Single<Boolean> checkPlayServices(Activity activity) {
//        return Single.just(true);
        if (activity != null && !activity.isFinishing()) {
            int playServicesStatus = PlayServicesUtils.playServicesStatus(activity);
            if (playServicesStatus == ConnectionResult.SUCCESS) {
                return Single.just(true);
            } else {
                return Single.error(new GooglePlayServicesNotAvailableException(playServicesStatus));
            }
        } else {
            return Single.just(false);
        }
    }

    @Override
    public Single<String> attest(String nonce) {
        return Single.create(emitter -> SafetyNet.getClient(context)
                .attest(Base64.decode(nonce, Base64.NO_WRAP), getSafetyNetKey())
                .addOnSuccessListener(response -> emitter.onSuccess(response.getJwsResult()))
                .addOnFailureListener(emitter::onError))
                .map(Object::toString);
    }

    private String getSafetyNetKey() {
        return SAFETY_NET_API_KEY1 +
                SAFETY_NET_API_KEY2 +
                SAFETY_NET_API_KEY3;
    }
}
