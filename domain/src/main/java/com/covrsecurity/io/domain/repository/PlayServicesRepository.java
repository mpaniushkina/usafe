package com.covrsecurity.io.domain.repository;

import android.app.Activity;

import io.reactivex.Single;

public interface PlayServicesRepository {

    Single<Boolean> checkPlayServices(Activity activity);

    Single<String> attest(String nonce); // SafetyNet
}
