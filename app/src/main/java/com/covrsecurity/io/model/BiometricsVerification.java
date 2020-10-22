package com.covrsecurity.io.model;

import com.covrsecurity.io.domain.entity.TransactionEntity;

import java.util.Arrays;

public class BiometricsVerification {

    private TransactionEntity transaction;
    private byte[] biometricsData;
    private boolean accept;

    public BiometricsVerification(TransactionEntity transaction, byte[] biometricsData, boolean accept) {
        this.transaction = transaction;
        this.biometricsData = biometricsData;
        this.accept = accept;
    }

    public TransactionEntity getTransaction() {
        return transaction;
    }

    public byte[] getBiometricsData() {
        return biometricsData;
    }

    public boolean isAccept() {
        return accept;
    }

    @Override
    public String toString() {
        return "BiometricsVerification{" +
                "transaction=" + transaction +
                ", biometricsData=" + Arrays.toString(biometricsData) +
                ", accept=" + accept +
                '}';
    }
}
