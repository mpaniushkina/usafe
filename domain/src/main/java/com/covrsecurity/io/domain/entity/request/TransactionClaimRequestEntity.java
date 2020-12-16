package com.covrsecurity.io.domain.entity.request;

public class TransactionClaimRequestEntity {
    private String referenceId;
    private String companyId;

    public TransactionClaimRequestEntity() {
    }

    public TransactionClaimRequestEntity(String referenceId, String companyId
    ) {
        this.referenceId = referenceId;
        this.companyId = companyId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyRegPublicKey) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "TransactionClaimRequestEntity{" +
                "referenceId='" + referenceId + '\'' +
                "companyId='" + companyId + '\'' +
                '}';
    }
}
