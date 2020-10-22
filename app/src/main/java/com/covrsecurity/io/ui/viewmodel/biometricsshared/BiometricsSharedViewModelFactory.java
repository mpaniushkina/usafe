package com.covrsecurity.io.ui.viewmodel.biometricsshared;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

public class BiometricsSharedViewModelFactory implements ViewModelProvider.Factory {

    @Inject
    public BiometricsSharedViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BiometricsSharedViewModel.class)) {
            return (T) new BiometricsSharedViewModel();
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
