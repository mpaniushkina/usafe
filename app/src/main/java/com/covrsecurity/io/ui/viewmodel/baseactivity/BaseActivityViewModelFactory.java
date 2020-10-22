package com.covrsecurity.io.ui.viewmodel.baseactivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.identity.IsApprovedUseCase;

import javax.inject.Inject;

public class BaseActivityViewModelFactory implements ViewModelProvider.Factory {

    protected final IsApprovedUseCase isApprovedUseCase;

    @Inject
    public BaseActivityViewModelFactory(IsApprovedUseCase isApprovedUseCase) {
        this.isApprovedUseCase = isApprovedUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BaseActivityViewModel.class)) {
            return (T) new BaseActivityViewModel(
                    isApprovedUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
