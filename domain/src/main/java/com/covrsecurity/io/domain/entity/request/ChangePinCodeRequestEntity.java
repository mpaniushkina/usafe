package com.covrsecurity.io.domain.entity.request;

import android.content.Context;

import java.util.Arrays;

public class ChangePinCodeRequestEntity {

    private Context context;
    private char[] oldCovrCode;
    private char[] newCovrCode;
    private boolean skipWeekPassword;

    public ChangePinCodeRequestEntity(Context context, char[] oldCovrCode, char[] newCovrCode, boolean skipWeekPassword) {
        this.context = context;
        this.oldCovrCode = oldCovrCode;
        this.newCovrCode = newCovrCode;
        this.skipWeekPassword = skipWeekPassword;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public char[] getOldCovrCode() {
        return oldCovrCode;
    }

    public void setOldCovrCode(char[] oldCovrCode) {
        this.oldCovrCode = oldCovrCode;
    }

    public char[] getNewCovrCode() {
        return newCovrCode;
    }

    public void setNewCovrCode(char[] newCovrCode) {
        this.newCovrCode = newCovrCode;
    }

    public boolean isSkipWeekPassword() {
        return skipWeekPassword;
    }

    public void setSkipWeekPassword(boolean skipWeekPassword) {
        this.skipWeekPassword = skipWeekPassword;
    }

    @Override
    public String toString() {
        return "ChangePinCodeRequestEntity{" +
                "context=" + context +
                ", oldCovrCode=" + Arrays.toString(oldCovrCode) +
                ", newCovrCode=" + Arrays.toString(newCovrCode) +
                ", skipWeekPassword=" + skipWeekPassword +
                '}';
    }
}
