package com.covrsecurity.io.domain.entity;

import java.io.Serializable;

public class BiometricEntity implements Serializable {

    private BiometricTypeEntity biometricType;
    private int maxAttemptCount;

    public BiometricEntity() {
    }

    public BiometricEntity(BiometricTypeEntity biometricType, int maxAttemptCount) {
        this.biometricType = biometricType;
        this.maxAttemptCount = maxAttemptCount;
    }

    public BiometricTypeEntity getBiometricType() {
        return biometricType;
    }

    public void setBiometricType(BiometricTypeEntity biometricType) {
        this.biometricType = biometricType;
    }

    public int getMaxAttemptCount() {
        return maxAttemptCount;
    }

    public void setMaxAttemptCount(int maxAttemptCount) {
        this.maxAttemptCount = maxAttemptCount;
    }

    @Override
    public String toString() {
        return "BiometricEntity{" +
                "biometricType=" + biometricType +
                ", maxAttemptCount=" + maxAttemptCount +
                '}';
    }
}
