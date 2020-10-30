package com.covrsecurity.io.inject.module;

import android.content.Context;

import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.manager.AppUnLockedSharedStateHandler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
abstract public class ApplicationModule {

    @Provides
    @Singleton
    public static Context context(IamApp app) {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    public static AppUnLockedSharedStateHandler provideAppUnLockedSharedStateHandler() {
        return new AppUnLockedSharedStateHandler();
    }
}
