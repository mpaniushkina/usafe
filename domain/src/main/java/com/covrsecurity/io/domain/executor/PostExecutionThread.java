package com.covrsecurity.io.domain.executor;

import io.reactivex.Scheduler;

public interface PostExecutionThread {

    Scheduler provideScheduler();
}
