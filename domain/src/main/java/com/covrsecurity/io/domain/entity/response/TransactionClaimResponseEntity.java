package com.covrsecurity.io.domain.entity.response;

public class TransactionClaimResponseEntity {
    private boolean isValid;

    public TransactionClaimResponseEntity(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return "TransactionClaimResponseEntity{" +
                "isValid=" + isValid +
                '}';
    }
}
