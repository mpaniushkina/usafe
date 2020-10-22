package com.covrsecurity.io.domain.entity.request;

import java.util.Arrays;

public class AssessPinCodeStrengthRequestEntity {

    private final char[] pinCode;

    public AssessPinCodeStrengthRequestEntity(char[] pinCode) {
        this.pinCode = pinCode;
    }

    public char[] getPinCode() {
        return pinCode;
    }

    @Override
    public String toString() {
        return "AssessPinCodeStrengthRequestEntity{" +
                "pinCode=" + Arrays.toString(pinCode) +
                '}';
    }
}
