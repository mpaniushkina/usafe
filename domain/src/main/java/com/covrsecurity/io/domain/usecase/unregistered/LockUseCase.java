package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;

import javax.inject.Inject;

import io.reactivex.Completable;

public class LockUseCase extends BaseUnregisteredRepositoryUseCase implements
        CompletableUseCase<Void> {

    @Inject
    public LockUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable Void aVoid) {
        return repository.lock()
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
