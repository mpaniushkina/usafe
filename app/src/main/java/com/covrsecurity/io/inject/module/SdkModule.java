package com.covrsecurity.io.inject.module;

import android.content.Context;

import com.covrsecurity.io.data.repository.IdentityRepositoryImpl;
import com.covrsecurity.io.data.repository.PlayServicesRepositoryImpl;
import com.covrsecurity.io.data.repository.RegisteredRepositoryImpl;
import com.covrsecurity.io.data.repository.TrueTimeRepositoryImpl;
import com.covrsecurity.io.data.repository.UnregisteredRepositoryImpl;
import com.covrsecurity.io.domain.repository.IdentityRepository;
import com.covrsecurity.io.domain.repository.PlayServicesRepository;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.repository.TrueTimeRepository;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;
import com.covrsecurity.io.sdk.CovrNewMainInterface;
import com.covrsecurity.io.common.ConstantsUtils;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.covrsecurity.io.utils.ConstantsUtils.COVR_DIRECTORY_TEMPLATE;

@Module
public class SdkModule {

    @Provides
    @Singleton
    public CovrNewMainInterface provideCovrNewMainInterface(Context context) {
        final String covrDirectoryPath = String.format(COVR_DIRECTORY_TEMPLATE, context.getFilesDir());
        final File covrDirectory = new File(covrDirectoryPath);
        return new CovrNewMainInterface.Builder().build(context, covrDirectory);
    }

    @Provides
    @Singleton
    public UnregisteredRepository provideUnregisteredRepository(CovrNewMainInterface covrInterface, Context context) {
        return new UnregisteredRepositoryImpl(covrInterface, context);
    }

    @Provides
    @Singleton
    public RegisteredRepository provideRegisteredRepository(CovrNewMainInterface covrInterface) {
        return new RegisteredRepositoryImpl(covrInterface);
    }

    @Provides
    @Singleton
    public IdentityRepository provideIdentityRepository() {
        return new IdentityRepositoryImpl(ConstantsUtils.CUSTOM_USER_AGENT);
    }

    @Provides
    @Singleton
    public PlayServicesRepository providePlayServicesRepository(Context context) {
        return new PlayServicesRepositoryImpl(context);
    }

    @Provides
    @Singleton
    public TrueTimeRepository provideTrueTimeRepository() {
        return new TrueTimeRepositoryImpl();
    }
}
