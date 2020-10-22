package com.covrsecurity.io.domain.entity.request;

public class MarkConnectionAsViewedRequestEntity {

    private String companyId;

    public MarkConnectionAsViewedRequestEntity(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "MarkConnectionAsViewedRequestEntity{" +
                "companyId='" + companyId + '\'' +
                '}';
    }
}
