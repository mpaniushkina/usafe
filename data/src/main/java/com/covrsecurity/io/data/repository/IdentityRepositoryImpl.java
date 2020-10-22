package com.covrsecurity.io.data.repository;

import com.covrsecurity.io.data.exception.SafetyNetNotVerified;
import com.covrsecurity.io.domain.entity.request.AttestationRequestEntity;
import com.covrsecurity.io.domain.entity.request.LogoutRequestEntity;
import com.covrsecurity.io.domain.entity.request.RecoverRequestEntity;
import com.covrsecurity.io.domain.entity.response.IsApprovedResponseEntity;
import com.covrsecurity.io.domain.exception.BlockCurrentAppException;
import com.covrsecurity.io.domain.exception.ForceUpdateException;
import com.covrsecurity.io.domain.repository.IdentityRepository;
import com.covrsecurity.io.network.request.identity.AttestationInputModel;
import com.covrsecurity.io.network.request.identity.LogOutRequest;
import com.covrsecurity.io.network.rx.ApiRxHolder;
import com.covrsecurity.io.network.rx.api.IdentityApiRx;

import io.reactivex.Completable;
import io.reactivex.Single;
import kotlin.NotImplementedError;
import retrofit2.HttpException;

import static com.covrsecurity.io.common.ConstantsUtils.HTTP_UPGRADE_REQUIRED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

public class IdentityRepositoryImpl implements IdentityRepository {

    private final IdentityApiRx identityApi;

    public IdentityRepositoryImpl(String userAgent) {
        this.identityApi = new ApiRxHolder(userAgent).getIdentityApi();
    }

    @Override
    public Single<String> getNonce(String deviceId) {
        return identityApi.getNonce(deviceId);
    }

    @Override
    public Single<Boolean> verify(AttestationRequestEntity requestEntity) {
        // TODO
        return Single.just(true);
        /*return Single.just(requestEntity)
                .map(entity -> new AttestationInputModel(entity.getStatement(), entity.getDeviceId()))
                .flatMap(identityApi::verify)
                .flatMap(verified -> {
                    if (verified) {
                        return Single.just(true);
                    } else {
                        return Single.error(new SafetyNetNotVerified());
                    }
                });*/
    }

    @Override
    public Single<IsApprovedResponseEntity> isApproved() {
        return identityApi.isApproved()
                .map(response -> new IsApprovedResponseEntity(response.getPlatform()))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpException) {
                        HttpException httpException = (HttpException) throwable;
                        switch (httpException.code()) {
                            case HTTP_UPGRADE_REQUIRED:
                                return Single.error(new ForceUpdateException());
                            case HTTP_FORBIDDEN:
                                return Single.error(new BlockCurrentAppException());
                            default:
                                return Single.error(httpException);
                        }
                    } else {
                        return Single.error(throwable);
                    }
                });
    }

    @Override
    public Completable logout(LogoutRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(request -> new LogOutRequest(request.getAccessToken(), request.getId(), request.getSecret()))
                .flatMapCompletable(logOutRequest -> identityApi.logout(requestEntity.getAccessToken(), logOutRequest));
    }
}
