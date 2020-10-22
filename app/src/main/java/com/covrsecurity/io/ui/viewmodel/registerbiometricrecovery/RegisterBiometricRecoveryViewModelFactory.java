package com.covrsecurity.io.ui.viewmodel.registerbiometricrecovery;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.RegisterBiometricRecoveryUseCase;

import javax.inject.Inject;

public class RegisterBiometricRecoveryViewModelFactory implements ViewModelProvider.Factory {

    private final RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase;

    @Inject
    public RegisterBiometricRecoveryViewModelFactory(RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase) {
        this.registerBiometricRecoveryUseCase = registerBiometricRecoveryUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterBiometricRecoveryViewModel.class)) {
            return (T) new RegisterBiometricRecoveryViewModel(
                    registerBiometricRecoveryUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
