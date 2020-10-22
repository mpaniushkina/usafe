package com.covrsecurity.io.domain.entity.request;

import java.util.Arrays;

public class RegisterRecoveryRequestEntity {

    private final byte[] biometricsBytes;
    private final byte[] imageIdCard;

    public RegisterRecoveryRequestEntity(byte[] biometricsBytes, byte[] imageIdCard) {
        this.biometricsBytes = biometricsBytes;
        this.imageIdCard = imageIdCard;
    }

    public byte[] getBiometricsBytes() {
        return biometricsBytes;
    }

    public byte[] getImageIdCard() {
        return imageIdCard;
    }

    @Override
    public String toString() {
        return "RegisterRecoveryRequestEntity{" +
                "biometricsBytes=" + Arrays.toString(biometricsBytes) +
                "imageIdCard=" + Arrays.toString(imageIdCard) +
                '}';
    }
}
