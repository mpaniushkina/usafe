package com.covrsecurity.io.domain.usecase.identity;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.response.IsApprovedResponseEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.IdentityRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class IsApprovedUseCase implements SingleUseCase<IsApprovedResponseEntity, Void> {

    private final Scheduler threadExecutorScheduler;
    private final Scheduler postExecutionThreadScheduler;

    private final IdentityRepository repository;

    @Inject
    public IsApprovedUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IdentityRepository repository) {
        this.threadExecutorScheduler = threadExecutor.provideScheduler();
        this.postExecutionThreadScheduler = postExecutionThread.provideScheduler();
        this.repository = repository;
    }

    @Override
    public Single<IsApprovedResponseEntity> execute(@Nullable Void aVoid) {
        return repository.isApproved()
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
