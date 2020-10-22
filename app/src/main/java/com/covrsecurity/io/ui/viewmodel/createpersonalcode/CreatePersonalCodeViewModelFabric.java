package com.covrsecurity.io.ui.viewmodel.createpersonalcode;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.unregistered.AssessPinCodeStrengthUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.RegisterUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.SetUpPasswordUseCase;

import javax.inject.Inject;

public class CreatePersonalCodeViewModelFabric implements ViewModelProvider.Factory {

    private final AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase;
    private final SetUpPasswordUseCase setUpPasswordUseCase;
    private final RegisterUseCase registerUseCase;

    @Inject
    public CreatePersonalCodeViewModelFabric(AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase, SetUpPasswordUseCase setUpPasswordUseCase, RegisterUseCase registerUseCase) {
        this.assessPinCodeStrengthUseCase = assessPinCodeStrengthUseCase;
        this.setUpPasswordUseCase = setUpPasswordUseCase;
        this.registerUseCase = registerUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreatePersonalCodeViewModel.class)) {
            return (T) new CreatePersonalCodeViewModel(
                    assessPinCodeStrengthUseCase,
                    setUpPasswordUseCase,
                    registerUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
