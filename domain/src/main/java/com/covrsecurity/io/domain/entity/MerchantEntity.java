package com.covrsecurity.io.domain.entity;

import java.io.Serializable;

public class MerchantEntity implements Serializable {

    private String id;
    private String userName;
    private long createdDate;
    private CompanyEntity company;
    private StatusEntity status;

    public MerchantEntity(String id, String userName, long createdDate, CompanyEntity company, StatusEntity status) {
        this.id = id;
        this.userName = userName;
        this.createdDate = createdDate;
        this.company = company;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }

    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MerchantEntity{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", createdDate=" + createdDate +
                ", company=" + company +
                ", status=" + status +
                '}';
    }
}
