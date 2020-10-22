package com.covrsecurity.io.domain.entity.request;

import java.util.Arrays;

public class ValidatePinCodeRequestEntity {

    private final char[] oldCovrCode;

    public ValidatePinCodeRequestEntity(char[] oldCovrCode) {
        this.oldCovrCode = oldCovrCode;
    }

    public char[] getOldCovrCode() {
        return oldCovrCode;
    }

    @Override
    public String toString() {
        return "ValidatePinCodeRequestEntity{" +
                "oldCovrCode=" + Arrays.toString(oldCovrCode) +
                '}';
    }
}
