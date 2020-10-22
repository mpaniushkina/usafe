package com.covrsecurity.io.domain.usecase.playservices;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.repository.PlayServicesRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class AttestSafetyNetUseCase implements SingleUseCase<String, String> {

    private final PlayServicesRepository repository;

    @Inject
    public AttestSafetyNetUseCase(PlayServicesRepository repository) {
        this.repository = repository;
    }

    @Override
    public Single<String> execute(@Nullable String nonce) {
        return repository.attest(nonce);
    }
}
