package com.covrsecurity.io.domain.entity.response;

public class QrCodeClaimResponseEntity {
    private boolean isValid;

    public QrCodeClaimResponseEntity(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return "QrCodeClaimResponse{" +
                "isValid=" + isValid +
                '}';
    }
}
