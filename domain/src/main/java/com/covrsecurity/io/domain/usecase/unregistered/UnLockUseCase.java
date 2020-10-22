package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.UnLockRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class UnLockUseCase extends BaseUnregisteredRepositoryUseCase implements
        SingleUseCase<Boolean, UnLockRequestEntity> {

    @Inject
    public UnLockUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<Boolean> execute(@Nullable UnLockRequestEntity requestEntity) {
        return repository.unLock(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
