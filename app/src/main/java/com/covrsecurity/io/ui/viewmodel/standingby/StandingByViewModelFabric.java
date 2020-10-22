package com.covrsecurity.io.ui.viewmodel.standingby;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.GetConnectionsUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetTransactionsUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetUnreadHistoryCountUseCase;
import com.covrsecurity.io.domain.usecase.registred.PostTransactionUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.CheckLockedUseCase;

import javax.inject.Inject;

public class StandingByViewModelFabric implements ViewModelProvider.Factory {

    private final CheckLockedUseCase checkLockedUseCase;
    private final GetConnectionsUseCase connectionsUseCase;
    private final GetTransactionsUseCase transactionsUseCase;
    private final GetUnreadHistoryCountUseCase unreadHistoryCountUseCase;
    private final PostTransactionUseCase postTransactionUseCase;

    @Inject
    public StandingByViewModelFabric(CheckLockedUseCase checkLockedUseCase, GetConnectionsUseCase connectionsUseCase, GetTransactionsUseCase transactionsUseCase, GetUnreadHistoryCountUseCase unreadHistoryCountUseCase, PostTransactionUseCase postTransactionUseCase) {
        this.checkLockedUseCase = checkLockedUseCase;
        this.connectionsUseCase = connectionsUseCase;
        this.transactionsUseCase = transactionsUseCase;
        this.unreadHistoryCountUseCase = unreadHistoryCountUseCase;
        this.postTransactionUseCase = postTransactionUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StandingByViewModel.class)) {
            return (T) new StandingByViewModel(
                    checkLockedUseCase,
                    connectionsUseCase,
                    transactionsUseCase,
                    unreadHistoryCountUseCase,
                    postTransactionUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
