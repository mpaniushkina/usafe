package com.covrsecurity.io.model.error;

public class BioMetricDataProvideCancel extends Exception {

    private final String transactionId;

    public BioMetricDataProvideCancel(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
