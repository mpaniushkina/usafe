package com.covrsecurity.io.domain.entity.request;

public class TransactionClaimRequestEntity {
    private String referenceId;
    private String companyRegPublicKey;

    public TransactionClaimRequestEntity() {
    }

    public TransactionClaimRequestEntity(String referenceId, String companyRegPublicKey) {
        this.referenceId = referenceId;
        this.companyRegPublicKey = companyRegPublicKey;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getCompanyRegPublicKey() {
        return companyRegPublicKey;
    }

    public void setExpiresAt(String companyRegPublicKey) {
        this.companyRegPublicKey = companyRegPublicKey;
    }

    @Override
    public String toString() {
        return "TransactionClaimRequestEntity{" +
                "referenceId='" + referenceId + '\'' +
                "companyRegPublicKey='" + companyRegPublicKey + '\'' +
                '}';
    }
}
