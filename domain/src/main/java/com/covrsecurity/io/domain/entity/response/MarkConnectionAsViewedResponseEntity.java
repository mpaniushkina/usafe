package com.covrsecurity.io.domain.entity.response;

public class MarkConnectionAsViewedResponseEntity {

    private boolean isSucceeded;

    public MarkConnectionAsViewedResponseEntity(boolean isSucceeded) {
        this.isSucceeded = isSucceeded;
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public void setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }

    @Override
    public String toString() {
        return "MarkConnectionAsViewedResponseEntity{" +
                "isSucceeded=" + isSucceeded +
                '}';
    }
}
