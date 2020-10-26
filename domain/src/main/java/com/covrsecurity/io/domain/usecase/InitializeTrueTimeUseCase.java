package com.covrsecurity.io.domain.usecase;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.TrueTimeRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class InitializeTrueTimeUseCase implements SingleUseCase<Date, Integer> {

    private final Scheduler threadExecutorScheduler;
    private final Scheduler postExecutionThreadScheduler;

    private final com.covrsecurity.io.domain.repository.TrueTimeRepository repository;

    @Inject
    public InitializeTrueTimeUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, TrueTimeRepository repository) {
        this.threadExecutorScheduler = threadExecutor.provideScheduler();
        this.postExecutionThreadScheduler = postExecutionThread.provideScheduler();
        this.repository = repository;
    }

    @Override
    public Single<Date> execute(@Nullable Integer retryCount) {
        return repository.initializeTrueTime(retryCount)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
