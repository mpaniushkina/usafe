package com.covrsecurity.io.domain.entity.response;

import com.covrsecurity.io.domain.entity.CompanyEntity;

public class TransactionClaimResponseEntity {
    private  String id;
    private  String userName;
    private  String createdDate;
    private CompanyEntity merchant;
    private  String status;

    public TransactionClaimResponseEntity(String id, String userName, String createdDate, CompanyEntity merchant, String status) {
        this.id = id;
        this.userName = userName;
        this.createdDate = createdDate;
        this.merchant = merchant;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public CompanyEntity getMerchant() {
        return merchant;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TransactionClaimResponseEntity{" +
                "id=" + id +
                "userName=" + userName +
                "createdDate=" + createdDate +
                "merchant=" + merchant +
                "status=" + status +
                '}';
    }
}
