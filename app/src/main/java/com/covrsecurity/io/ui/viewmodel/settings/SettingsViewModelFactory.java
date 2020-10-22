package com.covrsecurity.io.ui.viewmodel.settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.RegisterBiometricRecoveryUseCase;

import javax.inject.Inject;

public class SettingsViewModelFactory implements ViewModelProvider.Factory {

    private final RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase;

    @Inject
    public SettingsViewModelFactory(RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase) {
        this.registerBiometricRecoveryUseCase = registerBiometricRecoveryUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(
                    registerBiometricRecoveryUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
