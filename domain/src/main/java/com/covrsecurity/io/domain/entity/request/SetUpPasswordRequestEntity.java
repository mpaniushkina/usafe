package com.covrsecurity.io.domain.entity.request;

import java.util.Arrays;

public class SetUpPasswordRequestEntity {

    private final char[] covrCode;
    private final boolean skipWeekPassword;

    public SetUpPasswordRequestEntity(char[] covrCode, boolean skipWeekPassword) {
        this.covrCode = covrCode;
        this.skipWeekPassword = skipWeekPassword;
    }

    public char[] getCovrCode() {
        return covrCode;
    }

    public boolean isSkipWeekPassword() {
        return skipWeekPassword;
    }

    @Override
    public String toString() {
        return "SetUpPasswordRequestEntity{" +
                "covrCode=" + Arrays.toString(covrCode) +
                ", skipWeekPassword=" + skipWeekPassword +
                '}';
    }
}
