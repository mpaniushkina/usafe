package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.RecoverRequestEntity;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;

import javax.inject.Inject;

import io.reactivex.Completable;

public class RecoverUseCase extends BaseUnregisteredRepositoryUseCase implements
        CompletableUseCase<RecoverRequestEntity> {

    @Inject
    public RecoverUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable RecoverRequestEntity requestEntity) {
        return repository.recover(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
