package com.covrsecurity.io.domain.usecase.playservices;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.repository.PlayServicesRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class CheckPlayServicesUseCase implements SingleUseCase<Boolean, Activity> {

    private final PlayServicesRepository repository;

    @Inject
    public CheckPlayServicesUseCase(PlayServicesRepository repository) {
        this.repository = repository;
    }

    @Override
    public Single<Boolean> execute(@Nullable Activity activity) {
        return repository.checkPlayServices(activity);
    }
}
