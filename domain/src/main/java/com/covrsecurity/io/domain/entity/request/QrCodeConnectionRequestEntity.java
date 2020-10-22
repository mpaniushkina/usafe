package com.covrsecurity.io.domain.entity.request;

public class QrCodeConnectionRequestEntity {

    public String companyUserId;
    public String transactionId;

    public QrCodeConnectionRequestEntity(String companyUserId, String transactionId) {
        this.companyUserId = companyUserId;
        this.transactionId = transactionId;
    }

    public String getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(String companyUserId) {
        this.companyUserId = companyUserId;
    }

    public String getTransactionId() { return transactionId; }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "QrCodeConnectionRequestEntity{" +
                "companyUserId=" + companyUserId + '\'' +
                ", transactionId=" + transactionId + '\'' +
                '}';
    }
}
