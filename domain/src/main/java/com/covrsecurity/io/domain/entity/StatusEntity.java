package com.covrsecurity.io.domain.entity;

public enum StatusEntity {

    ACTIVE("Active"),
    EXPIRED("Expired"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    FAILED_BIOMETRIC("Failed biometric");

    private String value;

    StatusEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
