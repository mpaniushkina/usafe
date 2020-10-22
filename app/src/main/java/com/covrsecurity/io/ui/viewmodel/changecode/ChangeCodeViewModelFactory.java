package com.covrsecurity.io.ui.viewmodel.changecode;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.ChangePinCodeUseCase;
import com.covrsecurity.io.domain.usecase.registred.ValidatePinCodeUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.AssessPinCodeStrengthUseCase;

import javax.inject.Inject;

public class ChangeCodeViewModelFactory implements ViewModelProvider.Factory {

    private final AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase;
    private final ChangePinCodeUseCase changePinCodeUseCase;
    private final ValidatePinCodeUseCase validatePinCodeUseCase;

    @Inject
    public ChangeCodeViewModelFactory(AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase, ChangePinCodeUseCase changePinCodeUseCase, ValidatePinCodeUseCase validatePinCodeUseCase) {
        this.assessPinCodeStrengthUseCase = assessPinCodeStrengthUseCase;
        this.changePinCodeUseCase = changePinCodeUseCase;
        this.validatePinCodeUseCase = validatePinCodeUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChangeCodeViewModel.class)) {
            return (T) new ChangeCodeViewModel(
                    assessPinCodeStrengthUseCase,
                    changePinCodeUseCase,
                    validatePinCodeUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
