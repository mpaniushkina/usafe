package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.QrCodeConnectionRequestEntity;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;

import javax.inject.Inject;

import io.reactivex.Completable;

public class QrCodeConnectionUseCase extends BaseUnregisteredRepositoryUseCase implements
        CompletableUseCase<QrCodeConnectionRequestEntity>
//        SingleUseCase<QrCodeConnectionResponseEntity, QrCodeConnectionRequestEntity>
{

    @Inject
    public QrCodeConnectionUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable QrCodeConnectionRequestEntity requestEntity) {
        return repository.getQrCode(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }

//    @Override
//    public Single<QrCodeConnectionResponseEntity> execute(@Nullable QrCodeConnectionRequestEntity requestEntity) {
//        return repository.getQrCode(requestEntity)
//                .subscribeOn(threadExecutorScheduler)
//                .observeOn(postExecutionThreadScheduler);
//    }
}
