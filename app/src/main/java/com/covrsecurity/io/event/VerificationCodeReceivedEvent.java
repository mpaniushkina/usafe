package com.covrsecurity.io.event;

public class VerificationCodeReceivedEvent {

    private String mCode;

    public VerificationCodeReceivedEvent(String code) {
        this.mCode = code;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public String toString() {
        return "VerificationCodeReceivedEvent{" +
                "code='" + mCode + '\'' +
                '}';
    }
}
