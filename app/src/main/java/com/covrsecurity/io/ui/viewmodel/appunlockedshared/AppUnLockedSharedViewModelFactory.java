package com.covrsecurity.io.ui.viewmodel.appunlockedshared;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.manager.AppUnLockedSharedStateHandler;

import javax.inject.Inject;

public class AppUnLockedSharedViewModelFactory implements ViewModelProvider.Factory {

    private final AppUnLockedSharedStateHandler stateHandle;

    @Inject
    public AppUnLockedSharedViewModelFactory(AppUnLockedSharedStateHandler stateHandle) {
        this.stateHandle = stateHandle;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AppUnLockedSharedViewModel.class)) {
            return (T) new AppUnLockedSharedViewModel(stateHandle);
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
