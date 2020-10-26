package com.covrsecurity.io.domain.usecase.identity;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.usecase.base.SingleUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.IdentityRepository;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class GetNonceUseCase implements SingleUseCase<String, String> {

    private final Scheduler threadExecutorScheduler;
    private final Scheduler postExecutionThreadScheduler;

    private final com.covrsecurity.io.domain.repository.IdentityRepository repository;

    @Inject
    public GetNonceUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IdentityRepository repository) {
        this.threadExecutorScheduler = threadExecutor.provideScheduler();
        this.postExecutionThreadScheduler = postExecutionThread.provideScheduler();
        this.repository = repository;
    }

    @Override
    public Single<String> execute(@Nullable String deviceId) {
        return repository.getNonce(deviceId)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
