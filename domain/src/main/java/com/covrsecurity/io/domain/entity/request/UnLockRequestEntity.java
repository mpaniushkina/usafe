package com.covrsecurity.io.domain.entity.request;

import java.util.Arrays;

public class UnLockRequestEntity {

    private char[] covrCode;

    public UnLockRequestEntity( char[] covrCode) {
        this.covrCode = covrCode;
    }

    public char[] getCovrCode() {
        return covrCode;
    }

    public void setCovrCode(char[] covrCode) {
        this.covrCode = covrCode;
    }

    @Override
    public String toString() {
        return "UnLockRequestEntity{" +
                "covrCode=" + Arrays.toString(covrCode) +
                '}';
    }
}
