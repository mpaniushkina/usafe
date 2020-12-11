package com.covrsecurity.io.ui.viewmodel.myaccount;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.RegisterBiometricRecoveryUseCase;

import javax.inject.Inject;

public class MyAccountViewModelFactory implements ViewModelProvider.Factory {

    private final RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase;

    @Inject
    public MyAccountViewModelFactory(RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase) {
        this.registerBiometricRecoveryUseCase = registerBiometricRecoveryUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyAccountViewModel.class)) {
            return (T) new MyAccountViewModel(
                    registerBiometricRecoveryUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
