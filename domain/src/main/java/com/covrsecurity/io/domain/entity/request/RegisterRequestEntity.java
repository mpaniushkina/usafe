package com.covrsecurity.io.domain.entity.request;

import java.util.Arrays;

public class RegisterRequestEntity {

    private final char[] covrCode;
    private final boolean skipWeekPassword;
    private final byte[] biometricsData;
    private final byte[] imageIdCard;

    public RegisterRequestEntity(char[] covrCode, boolean skipWeekPassword, byte[] biometricsData, byte[] imageIdCard) {
        this.covrCode = covrCode;
        this.skipWeekPassword = skipWeekPassword;
        this.biometricsData = biometricsData;
        this.imageIdCard = imageIdCard;
    }

    public char[] getCovrCode() {
        return covrCode;
    }

    public boolean isSkipWeekPassword() {
        return skipWeekPassword;
    }

    public byte[] getBiometricsData() {
        return biometricsData;
    }

    public byte[] getImageIdCard() {
        return imageIdCard;
    }

    @Override
    public String toString() {
        return "RegisterRequestEntity{" +
                "covrCode=" + Arrays.toString(covrCode) +
                ", skipWeekPassword=" + skipWeekPassword +
                ", biometricsData=" + Arrays.toString(biometricsData) +
                ", imageIdCard=" + Arrays.toString(imageIdCard) +
                '}';
    }
}
