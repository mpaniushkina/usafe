package com.covrsecurity.io.data.repository;

import android.content.Context;

import com.covrsecurity.io.common.utils.FileUtils;
import com.covrsecurity.io.data.utils.EntityMapper;
import com.covrsecurity.io.domain.entity.request.AssessPinCodeStrengthRequestEntity;
import com.covrsecurity.io.domain.entity.request.QrCodeConnectionRequestEntity;
import com.covrsecurity.io.domain.entity.request.RecoverRequestEntity;
import com.covrsecurity.io.domain.entity.request.RegisterRequestEntity;
import com.covrsecurity.io.domain.entity.request.SetUpPasswordRequestEntity;
import com.covrsecurity.io.domain.entity.request.UnLockRequestEntity;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.domain.entity.response.QrCodeConnectionResponseEntity;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;
import com.covrsecurity.io.sdk.CovrNewMainInterface;
import com.covrsecurity.io.sdk.request.QrCodeConnectionRequest;
import com.covrsecurity.io.sdk.request.RecoveryRequest;
import com.covrsecurity.io.sdk.request.RegisterRequest;
import com.covrsecurity.io.sdk.request.SetUpPasswordRequest;
import com.covrsecurity.io.sdk.response.QrCodeConnectionResponse;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.covrsecurity.io.common.utils.ArrayUtils.arrayContains;

public class UnregisteredRepositoryImpl implements UnregisteredRepository {

    private static final String[] DIRECTORIES_TO_PERSIST = new String[]{
            "databases",
            "no_backup",
            "lib",
            "shared_prefs"
    };

    private final CovrNewMainInterface covrInterface;
    private final Context context;

    @Inject
    public UnregisteredRepositoryImpl(CovrNewMainInterface covrInterface, Context context) {
        this.covrInterface = covrInterface;
        this.context = context;
    }

    @Override
    public Single<Boolean> isRegistered() {
        return covrInterface.isRegistered();
    }

    @Override
    public Completable clear() {
        return covrInterface.clear()
                .andThen(Completable.fromAction(this::clearApplicationData));
    }

    // TODO refactor. Make own repository
    private void clearApplicationData() {
        final File applicationDirectory = new File(context.getCacheDir().getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!arrayContains(DIRECTORIES_TO_PERSIST, fileName)) {
                    FileUtils.deleteAllFilesInDirectory(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    @Override
    public Completable register(RegisterRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(request -> new RegisterRequest(request.getCovrCode(), request.getBiometricsData(), request.getImageIdCard(), request.isSkipWeekPassword()))
                .flatMapCompletable(covrInterface::register);
    }

    @Override
    public Completable getQrCode(QrCodeConnectionRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(request -> new QrCodeConnectionRequest(request.getCompanyUserId(), request.getTransactionId()))
                .flatMapCompletable(covrInterface::getQrCode);
    }

//    @Override
//    public Single<QrCodeConnectionResponse> getQrCode(QrCodeConnectionRequestEntity requestEntity) {
//        return Single.just(requestEntity)
//                .map(request -> new QrCodeConnectionRequest(request.getCompanyUserId(), request.getTransactionId()))
//                .flatMap(covrInterface::getQrCode);
//    }

    @Override
    public Completable setUpPassword(SetUpPasswordRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(request -> new SetUpPasswordRequest(request.getCovrCode(), request.isSkipWeekPassword()))
                .flatMapCompletable(covrInterface::setUpPassword);
    }

    @Override
    public Single<Boolean> isUnlocked() {
        return covrInterface.isUnlocked();
    }

    @Override
    public Single<AppUnlockTimeEntity> appUnlockTime() {
        return covrInterface.appUnlockTime()
                .map(EntityMapper::getAppUnlockTimeEntity);
    }

    @Override
    public Completable lock() {
        return covrInterface.lock();
    }

    @Override
    public Single<Boolean> unLock(UnLockRequestEntity requestEntity) {
        return covrInterface.unLock(requestEntity.getCovrCode());
    }

    @Override
    public Completable assessPinCodeStrength(AssessPinCodeStrengthRequestEntity requestEntity) {
        return covrInterface.assessPinCodeStrength(requestEntity.getPinCode());
    }

    @Override
    public Completable recover(RecoverRequestEntity requestEntity) {
        return Single.just(RecoveryRequest.getImageRecoveryRequest(
                requestEntity.getRecoverId(),
                requestEntity.getBiometricsData()
        )).flatMapCompletable(covrInterface::recoverIdentity);
    }
}
