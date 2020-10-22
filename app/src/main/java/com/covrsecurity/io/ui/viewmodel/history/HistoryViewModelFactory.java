package com.covrsecurity.io.ui.viewmodel.history;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.GetTransactionHistoryUseCase;
import com.covrsecurity.io.domain.usecase.registred.MarkHistoryAsViewedUseCase;

import javax.inject.Inject;

public class HistoryViewModelFactory implements ViewModelProvider.Factory {

    private final GetTransactionHistoryUseCase transactionHistoryUseCase;
    private final MarkHistoryAsViewedUseCase markHistoryAsViewedUseCase;

    @Inject
    public HistoryViewModelFactory(GetTransactionHistoryUseCase transactionHistoryUseCase, MarkHistoryAsViewedUseCase markHistoryAsViewedUseCase) {
        this.transactionHistoryUseCase = transactionHistoryUseCase;
        this.markHistoryAsViewedUseCase = markHistoryAsViewedUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HistoryViewModel.class)) {
            return (T) new HistoryViewModel(
                    transactionHistoryUseCase,
                    markHistoryAsViewedUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
