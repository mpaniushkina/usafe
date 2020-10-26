package com.covrsecurity.io.domain.entity;

import java.io.Serializable;

public class CompanyEntity implements Serializable {

    private String id;
    private String userName;
    private String publicKey;
    private long createdDate;
    private String companyId;
    private String name;
    private String fullName;
    private String websiteUrl;
    private String websiteName;
    private String logo;
    private boolean active;
    private boolean isViewed;
    private com.covrsecurity.io.domain.entity.StatusEntity status;

    public CompanyEntity(String id, String userName, String publicKey, long createdDate, String companyId, String name, String fullName, String websiteUrl, String websiteName, String logo, boolean active, boolean isViewed, com.covrsecurity.io.domain.entity.StatusEntity status) {
        this.id = id;
        this.userName = userName;
        this.publicKey = publicKey;
        this.createdDate = createdDate;
        this.companyId = companyId;
        this.name = name;
        this.fullName = fullName;
        this.websiteUrl = websiteUrl;
        this.websiteName = websiteName;
        this.logo = logo;
        this.active = active;
        this.isViewed = isViewed;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public com.covrsecurity.io.domain.entity.StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CompanyEntity{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", createdDate=" + createdDate +
                ", companyId='" + companyId + '\'' +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", websiteName='" + websiteName + '\'' +
                ", logo='" + logo + '\'' +
                ", active=" + active +
                ", isViewed=" + isViewed +
                ", status=" + status +
                '}';
    }
}
