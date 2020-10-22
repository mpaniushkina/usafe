package com.covrsecurity.io.domain.entity.response;

public class MarkHistoryAsViewedResponseEntity {

    private boolean isSucceeded;

    public MarkHistoryAsViewedResponseEntity(boolean isSucceeded) {
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
        return "MarkHistoryAsViewedResponseEntity{" +
                "isSucceeded=" + isSucceeded +
                '}';
    }
}
