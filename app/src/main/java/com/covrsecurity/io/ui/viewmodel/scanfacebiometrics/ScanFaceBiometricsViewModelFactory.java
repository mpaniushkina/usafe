package com.covrsecurity.io.ui.viewmodel.scanfacebiometrics;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.unregistered.RecoverUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.RegisterUseCase;

import javax.inject.Inject;

public class ScanFaceBiometricsViewModelFactory implements ViewModelProvider.Factory {

    private final RegisterUseCase registerUseCase;
    private final RecoverUseCase recoverUseCase;

    @Inject
    public ScanFaceBiometricsViewModelFactory(RegisterUseCase registerUseCase, RecoverUseCase recoverUseCase) {
        this.registerUseCase = registerUseCase;
        this.recoverUseCase = recoverUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ScanFaceBiometricsViewModel.class)) {
            return (T) new ScanFaceBiometricsViewModel(
                    registerUseCase,
                    recoverUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
