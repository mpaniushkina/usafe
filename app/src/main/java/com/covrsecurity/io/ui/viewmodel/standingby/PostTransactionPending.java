package com.covrsecurity.io.ui.viewmodel.standingby;

import com.covrsecurity.io.domain.entity.TransactionEntity;

public class PostTransactionPending {

    private TransactionEntity pendingTransaction;
    private boolean accept;

    public PostTransactionPending(TransactionEntity pendingTransaction, boolean accept) {
        this.pendingTransaction = pendingTransaction;
        this.accept = accept;
    }

    public TransactionEntity getPendingTransaction() {
        return pendingTransaction;
    }

    public boolean isAccept() {
        return accept;
    }
}