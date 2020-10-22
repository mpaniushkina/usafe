package com.covrsecurity.io.ui.viewmodel.lockscreent;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.unregistered.GetAppUnlockTimeUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.UnLockUseCase;

import javax.inject.Inject;

public class LockScreenViewModelFactory implements ViewModelProvider.Factory {

    private final GetAppUnlockTimeUseCase getAppUnlockTimeUseCase;
    private final UnLockUseCase unLockUseCase;

    @Inject
    public LockScreenViewModelFactory(GetAppUnlockTimeUseCase getAppUnlockTimeUseCase, UnLockUseCase unLockUseCase) {
        this.getAppUnlockTimeUseCase = getAppUnlockTimeUseCase;
        this.unLockUseCase = unLockUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LockScreenViewModel.class)) {
            return (T) new LockScreenViewModel(
                    getAppUnlockTimeUseCase,
                    unLockUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
