package com.covrsecurity.io.domain.repository;

import com.covrsecurity.io.domain.entity.request.AttestationRequestEntity;
import com.covrsecurity.io.domain.entity.request.LogoutRequestEntity;
import com.covrsecurity.io.domain.entity.response.IsApprovedResponseEntity;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface IdentityRepository {

    Single<String> getNonce(String deviceId);

    Single<Boolean> verify(AttestationRequestEntity requestEntity);

    Single<IsApprovedResponseEntity> isApproved();

    Completable logout(LogoutRequestEntity requestEntity);
}
