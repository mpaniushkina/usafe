package com.covrsecurity.io.domain.usecase.base;

import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;

import io.reactivex.Scheduler;

public abstract class BaseRepositoryUseCase {

    protected final Scheduler threadExecutorScheduler;
    protected final Scheduler postExecutionThreadScheduler;

    public BaseRepositoryUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutorScheduler = threadExecutor.provideScheduler();
        this.postExecutionThreadScheduler = postExecutionThread.provideScheduler();
    }
}
