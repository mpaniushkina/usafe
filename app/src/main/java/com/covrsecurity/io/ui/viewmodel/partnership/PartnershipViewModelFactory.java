package com.covrsecurity.io.ui.viewmodel.partnership;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.AddConnectionUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetConnectionsUseCase;
import com.covrsecurity.io.domain.usecase.registred.QrCodeClaimUseCase;
import com.covrsecurity.io.domain.usecase.registred.QrCodeLoginUseCase;
import com.covrsecurity.io.domain.usecase.registred.QrCodeReuseUseCase;
import com.covrsecurity.io.domain.usecase.registred.TransactionClaimCompleteUseCase;
import com.covrsecurity.io.domain.usecase.registred.TransactionReuseCompleteUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.QrCodeConnectionUseCase;

import javax.inject.Inject;

public class PartnershipViewModelFactory implements ViewModelProvider.Factory {

    private final GetConnectionsUseCase getConnectionsUseCase;
    private final AddConnectionUseCase addConnectionUseCase;
    private final QrCodeConnectionUseCase qrCodeConnectionUseCase;
    private final QrCodeClaimUseCase qrCodeClaimUseCase;
    private final TransactionClaimCompleteUseCase transactionClaimCompleteUseCase;
    private final QrCodeReuseUseCase qrCodeReuseUseCase;
    private final TransactionReuseCompleteUseCase transactionReuseCompleteUseCase;
    private final QrCodeLoginUseCase qrCodeLoginUseCase;

    @Inject
    public PartnershipViewModelFactory(GetConnectionsUseCase getConnectionsUseCase, AddConnectionUseCase addConnectionUseCase,
                                       QrCodeConnectionUseCase qrCodeConnectionUseCase, QrCodeClaimUseCase qrCodeClaimUseCase,
                                       TransactionClaimCompleteUseCase transactionClaimCompleteUseCase, QrCodeReuseUseCase qrCodeReuseUseCase,
                                       TransactionReuseCompleteUseCase transactionReuseCompleteUseCase, QrCodeLoginUseCase qrCodeLoginUseCase) {
        this.getConnectionsUseCase = getConnectionsUseCase;
        this.addConnectionUseCase = addConnectionUseCase;
        this.qrCodeConnectionUseCase = qrCodeConnectionUseCase;
        this.qrCodeClaimUseCase = qrCodeClaimUseCase;
        this.transactionClaimCompleteUseCase = transactionClaimCompleteUseCase;
        this.qrCodeReuseUseCase = qrCodeReuseUseCase;
        this.transactionReuseCompleteUseCase = transactionReuseCompleteUseCase;
        this.qrCodeLoginUseCase = qrCodeLoginUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PartnershipViewModel.class)) {
            return (T) new PartnershipViewModel(
                    getConnectionsUseCase,
                    addConnectionUseCase,
                    qrCodeConnectionUseCase,
                    qrCodeClaimUseCase,
                    transactionClaimCompleteUseCase,
                    qrCodeReuseUseCase,
                    transactionReuseCompleteUseCase,
                    qrCodeLoginUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
