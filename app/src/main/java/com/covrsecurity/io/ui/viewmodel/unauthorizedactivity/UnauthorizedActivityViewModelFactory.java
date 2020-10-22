package com.covrsecurity.io.ui.viewmodel.unauthorizedactivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.covrsecurity.io.domain.usecase.identity.IsApprovedUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.ClearUseCase;
import com.covrsecurity.io.ui.viewmodel.baseactivity.BaseActivityViewModelFactory;

import javax.inject.Inject;

public class UnauthorizedActivityViewModelFactory extends BaseActivityViewModelFactory {

    private final ClearUseCase clearUseCase;

    @Inject
    public UnauthorizedActivityViewModelFactory(IsApprovedUseCase isApprovedUseCase, ClearUseCase clearUseCase) {
        super(isApprovedUseCase);
        this.clearUseCase = clearUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UnauthorizedActivityViewModel.class)) {
            return (T) new UnauthorizedActivityViewModel(
                    isApprovedUseCase,
                    clearUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}