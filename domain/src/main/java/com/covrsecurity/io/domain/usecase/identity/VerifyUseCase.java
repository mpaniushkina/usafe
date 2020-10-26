package com.covrsecurity.io.domain.usecase.identity;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.AttestationRequestEntity;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.IdentityRepository;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class VerifyUseCase implements SingleUseCase<Boolean, AttestationRequestEntity> {

    private final Scheduler threadExecutorScheduler;
    private final Scheduler postExecutionThreadScheduler;

    private final com.covrsecurity.io.domain.repository.IdentityRepository repository;

    @Inject
    public VerifyUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IdentityRepository repository) {
        this.threadExecutorScheduler = threadExecutor.provideScheduler();
        this.postExecutionThreadScheduler = postExecutionThread.provideScheduler();
        this.repository = repository;
    }

    @Override
    public Single<Boolean> execute(@Nullable AttestationRequestEntity requestEntity) {
        return repository.verify(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
