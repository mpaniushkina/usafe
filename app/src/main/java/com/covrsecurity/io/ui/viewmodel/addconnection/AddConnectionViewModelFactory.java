package com.covrsecurity.io.ui.viewmodel.addconnection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.AddConnectionUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.QrCodeConnectionUseCase;

import javax.inject.Inject;

public class AddConnectionViewModelFactory implements ViewModelProvider.Factory {

    private final AddConnectionUseCase addConnectionUseCase;
    private final QrCodeConnectionUseCase qrCodeConnectionUseCase;

    @Inject
    public AddConnectionViewModelFactory(AddConnectionUseCase addConnectionUseCase, QrCodeConnectionUseCase qrCodeConnectionUseCase) {
        this.addConnectionUseCase = addConnectionUseCase;
        this.qrCodeConnectionUseCase = qrCodeConnectionUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddConnectionViewModel.class)) {
            return (T) new AddConnectionViewModel(
                    addConnectionUseCase,
                    qrCodeConnectionUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
