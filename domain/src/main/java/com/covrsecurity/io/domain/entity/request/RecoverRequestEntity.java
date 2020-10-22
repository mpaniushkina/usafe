package com.covrsecurity.io.domain.entity.request;

import java.util.Arrays;

public class RecoverRequestEntity {

    private final String recoverId;
    private final byte[] biometricsData;

    public RecoverRequestEntity(String recoverId, byte[] biometricsData) {
        this.recoverId = recoverId;
        this.biometricsData = biometricsData;
    }

    public String getRecoverId() {
        return recoverId;
    }

    public byte[] getBiometricsData() {
        return biometricsData;
    }

    @Override
    public String toString() {
        return "RecoverRequestEntity{" +
                "recoverId='" + recoverId + '\'' +
                ", biometricsData=" + Arrays.toString(biometricsData) +
                '}';
    }
}
