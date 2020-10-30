package com.covrsecurity.io.inject.component;

import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.inject.module.ActivityModule;
import com.covrsecurity.io.inject.module.ApplicationModule;
import com.covrsecurity.io.inject.module.AuthorizedFragmentModule;
import com.covrsecurity.io.inject.module.DomainModule;
import com.covrsecurity.io.inject.module.SdkModule;
import com.covrsecurity.io.inject.module.ServiceModule;
import com.covrsecurity.io.inject.module.UnauthorizedFragmentModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        ApplicationModule.class,
        AndroidSupportInjectionModule.class,
        DomainModule.class,
        SdkModule.class,
        ActivityModule.class,
        UnauthorizedFragmentModule.class,
        AuthorizedFragmentModule.class,
        ServiceModule.class})
@Singleton
public interface AppComponent extends AndroidInjector<IamApp> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<IamApp> {
    }
}