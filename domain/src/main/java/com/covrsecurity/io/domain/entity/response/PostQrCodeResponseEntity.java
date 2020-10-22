package com.covrsecurity.io.domain.entity.response;

import com.covrsecurity.io.domain.entity.CompanyEntity;

public class PostQrCodeResponseEntity {

    private String id;
    private String userName;
    private String createdDate;
    private CompanyEntity company;
    private String status;

    public PostQrCodeResponseEntity(String id, String userName, String createdDate, CompanyEntity company, String status) {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PostQrCodeResponseEntity{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", company=" + company +
                ", status=" + status +
                '}';
    }
}
