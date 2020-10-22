package com.covrsecurity.io.ui.viewmodel.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

public class StubViewModelFactory implements ViewModelProvider.Factory {

    @Inject
    public StubViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StubViewModel.class)) {
            return (T) new StubViewModel();
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
