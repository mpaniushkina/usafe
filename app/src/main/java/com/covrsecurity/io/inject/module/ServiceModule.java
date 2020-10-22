package com.covrsecurity.io.inject.module;

import com.covrsecurity.io.app.fcm.RegistrationIntentService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract RegistrationIntentService contributeRegistrationIntentService();
}
