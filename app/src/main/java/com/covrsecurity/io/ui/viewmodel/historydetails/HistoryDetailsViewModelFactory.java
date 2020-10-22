package com.covrsecurity.io.ui.viewmodel.historydetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.GetTransactionDetailsUseCase;

import javax.inject.Inject;

public class HistoryDetailsViewModelFactory implements ViewModelProvider.Factory {

    private final GetTransactionDetailsUseCase getTransactionDetailsUseCase;

    @Inject
    public HistoryDetailsViewModelFactory(GetTransactionDetailsUseCase getTransactionDetailsUseCase) {
        this.getTransactionDetailsUseCase = getTransactionDetailsUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HistoryDetailsViewModel.class)) {
            return (T) new HistoryDetailsViewModel(
                    getTransactionDetailsUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
