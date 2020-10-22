package com.covrsecurity.io.ui.viewmodel.authorizedactivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.covrsecurity.io.domain.usecase.identity.IsApprovedUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetPushNotificationsUseCase;
import com.covrsecurity.io.ui.viewmodel.baseactivity.BaseActivityViewModelFactory;

import javax.inject.Inject;

public class AuthorizedActivityViewModelFactory extends BaseActivityViewModelFactory {

    private final GetPushNotificationsUseCase pushNotificationsUseCase;

    @Inject
    public AuthorizedActivityViewModelFactory(IsApprovedUseCase isApprovedUseCase, GetPushNotificationsUseCase pushNotificationsUseCase) {
        super(isApprovedUseCase);
        this.pushNotificationsUseCase = pushNotificationsUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthorizedActivityViewModel.class)) {
            return (T) new AuthorizedActivityViewModel(
                    isApprovedUseCase,
                    pushNotificationsUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
