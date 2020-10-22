package com.covrsecurity.io.domain.usecase;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class DelayUseCase implements CompletableUseCase<Long> {

    private final Scheduler threadExecutorScheduler;
    private final Scheduler postExecutionThreadScheduler;

    @Inject
    public DelayUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutorScheduler = threadExecutor.provideScheduler();
        this.postExecutionThreadScheduler = postExecutionThread.provideScheduler();
    }

    @Override
    public Completable execute(@Nullable Long delay) {
        return Completable.complete()
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
