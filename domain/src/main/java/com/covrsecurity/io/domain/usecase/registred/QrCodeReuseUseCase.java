package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.QrCodeClaimRequestEntity;
import com.covrsecurity.io.domain.entity.response.QrCodeClaimResponseEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class QrCodeReuseUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<QrCodeClaimResponseEntity, QrCodeClaimRequestEntity> {

    @Inject
    public QrCodeReuseUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<QrCodeClaimResponseEntity> execute(@Nullable QrCodeClaimRequestEntity request) {
        return repository.reuseQrCode(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
