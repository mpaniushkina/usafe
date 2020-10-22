package com.covrsecurity.io.inject.module;

import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class DomainModule {

    @Provides
    @Singleton
    public ThreadExecutor providesThreadExecutor() {
        return Schedulers::io;
    }

    @Provides
    @Singleton
    public PostExecutionThread providesPostExecutionThread() {
        return AndroidSchedulers::mainThread;
    }
}