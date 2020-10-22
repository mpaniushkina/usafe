package com.covrsecurity.io.domain.repository;

import com.covrsecurity.io.domain.entity.request.AssessPinCodeStrengthRequestEntity;
import com.covrsecurity.io.domain.entity.request.QrCodeConnectionRequestEntity;
import com.covrsecurity.io.domain.entity.request.RecoverRequestEntity;
import com.covrsecurity.io.domain.entity.request.RegisterRequestEntity;
import com.covrsecurity.io.domain.entity.request.SetUpPasswordRequestEntity;
import com.covrsecurity.io.domain.entity.request.UnLockRequestEntity;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface UnregisteredRepository {

    Single<Boolean> isRegistered();

    Completable clear();

    Completable register(RegisterRequestEntity requestEntity);

    Completable setUpPassword(SetUpPasswordRequestEntity requestEntity);

    Single<Boolean> isUnlocked();

    Single<AppUnlockTimeEntity> appUnlockTime();

    Completable lock();

    Single<Boolean> unLock(UnLockRequestEntity requestEntity);

    Completable assessPinCodeStrength(AssessPinCodeStrengthRequestEntity requestEntity);

    Completable recover(RecoverRequestEntity recoverRequestEntity);

    Completable getQrCode(QrCodeConnectionRequestEntity requestEntity);

//    Single getQrCode(QrCodeConnectionRequestEntity requestEntity);
}
