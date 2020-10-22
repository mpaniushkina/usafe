package com.covrsecurity.io.domain.usecase.identity;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.LogoutRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.IdentityRepository;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class LogoutUseCase implements CompletableUseCase<LogoutRequestEntity> {

    private final Scheduler threadExecutorScheduler;
    private final Scheduler postExecutionThreadScheduler;

    private final IdentityRepository repository;

    @Inject
    public LogoutUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IdentityRepository repository) {
        this.threadExecutorScheduler = threadExecutor.provideScheduler();
        this.postExecutionThreadScheduler = postExecutionThread.provideScheduler();
        this.repository = repository;
    }

    @Override
    public Completable execute(@Nullable LogoutRequestEntity logoutRequestEntity) {
        return repository.logout(logoutRequestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
