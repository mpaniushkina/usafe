package com.covrsecurity.io.domain.entity.request;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.TransactionEntity;

import java.util.Arrays;

public class TransactionConfirmationRequestEntity {

    private TransactionEntity transaction;
    private boolean accept;
    @Nullable
    private byte[] bioMetricData;

    public TransactionConfirmationRequestEntity(TransactionEntity transaction, boolean accept, @Nullable byte[] bioMetricData) {
        this.transaction = transaction;
        this.accept = accept;
        this.bioMetricData = bioMetricData;
    }

    public TransactionEntity getTransaction() {
        return transaction;
    }

    public boolean isAccept() {
        return accept;
    }

    @Nullable
    public byte[] getBioMetricData() {
        return bioMetricData;
    }

    @Override
    public String toString() {
        return "TransactionConfirmationRequestEntity{" +
                "transaction=" + transaction +
                ", accept=" + accept +
                ", bioMetricData=" + Arrays.toString(bioMetricData) +
                '}';
    }
}
