package com.covrsecurity.io.domain.entity.request;

public class GetTransactionDetailsRequestEntity {

    private String transactionId;
    private String companyId;

    public GetTransactionDetailsRequestEntity(String transactionId, String companyId) {
        this.transactionId = transactionId;
        this.companyId = companyId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "GetTransactionDetailsRequestEntity{" +
                "transactionId='" + transactionId + '\'' +
                ", companyId='" + companyId + '\'' +
                '}';
    }
}
