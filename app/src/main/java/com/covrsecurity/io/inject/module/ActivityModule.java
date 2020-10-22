package com.covrsecurity.io.inject.module;

import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract UnauthorizedActivity contributeUnauthorizedActivity();

    @ContributesAndroidInjector
    abstract AuthorizedActivity contributeAuthorizedActivity();
}