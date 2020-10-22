package com.covrsecurity.io.domain.entity;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class TransactionEntity implements Serializable {

    private String id;
    private CompanyEntity company;
    private String companyClientId;
    private String templateId;
    private TemplateEntity template;
    private long validTo;
    private long validFrom;
    private StatusEntity status;
    private String createdByIp;
    private String verifiedByIp;
    private long created;
    private long updatedAt;
    private String acceptHistoryMessage;
    private String rejectHistoryMessage;
    private String expiredHistoryMessage;
    private String failedBiometricHistoryMessage;
    private RequestEntity request;
    private boolean isViewed;
    private NtSignatureEntity signature;
    @Nullable
    private BiometricEntity biometric;

    public TransactionEntity(String id, CompanyEntity company, String companyClientId, String templateId, TemplateEntity template, long validTo, long validFrom, StatusEntity status, String createdByIp, String verifiedByIp, long created, long updatedAt, String acceptHistoryMessage, String rejectHistoryMessage, String expiredHistoryMessage, String failedBiometricHistoryMessage, RequestEntity request, boolean isViewed, NtSignatureEntity signature, @Nullable BiometricEntity biometric) {
        this.id = id;
        this.company = company;
        this.companyClientId = companyClientId;
        this.templateId = templateId;
        this.template = template;
        this.validTo = validTo;
        this.validFrom = validFrom;
        this.status = status;
        this.createdByIp = createdByIp;
        this.verifiedByIp = verifiedByIp;
        this.created = created;
        this.updatedAt = updatedAt;
        this.acceptHistoryMessage = acceptHistoryMessage;
        this.rejectHistoryMessage = rejectHistoryMessage;
        this.expiredHistoryMessage = expiredHistoryMessage;
        this.failedBiometricHistoryMessage = failedBiometricHistoryMessage;
        this.request = request;
        this.isViewed = isViewed;
        this.signature = signature;
        this.biometric = biometric;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }

    public String getCompanyClientId() {
        return companyClientId;
    }

    public void setCompanyClientId(String companyClientId) {
        this.companyClientId = companyClientId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public void setTemplate(TemplateEntity template) {
        this.template = template;
    }

    public long getValidTo() {
        return validTo;
    }

    public void setValidTo(long validTo) {
        this.validTo = validTo;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    public String getCreatedByIp() {
        return createdByIp;
    }

    public void setCreatedByIp(String createdByIp) {
        this.createdByIp = createdByIp;
    }

    public String getVerifiedByIp() {
        return verifiedByIp;
    }

    public void setVerifiedByIp(String verifiedByIp) {
        this.verifiedByIp = verifiedByIp;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAcceptHistoryMessage() {
        return acceptHistoryMessage;
    }

    public void setAcceptHistoryMessage(String acceptHistoryMessage) {
        this.acceptHistoryMessage = acceptHistoryMessage;
    }

    public String getRejectHistoryMessage() {
        return rejectHistoryMessage;
    }

    public void setRejectHistoryMessage(String rejectHistoryMessage) {
        this.rejectHistoryMessage = rejectHistoryMessage;
    }

    public String getExpiredHistoryMessage() {
        return expiredHistoryMessage;
    }

    public void setExpiredHistoryMessage(String expiredHistoryMessage) {
        this.expiredHistoryMessage = expiredHistoryMessage;
    }

    public String getFailedBiometricHistoryMessage() {
        return failedBiometricHistoryMessage;
    }

    public void setFailedBiometricHistoryMessage(String failedBiometricHistoryMessage) {
        this.failedBiometricHistoryMessage = failedBiometricHistoryMessage;
    }

    public RequestEntity getRequest() {
        return request;
    }

    public void setRequest(RequestEntity request) {
        this.request = request;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public NtSignatureEntity getSignature() {
        return signature;
    }

    public void setSignature(NtSignatureEntity signature) {
        this.signature = signature;
    }

    @Nullable
    public BiometricEntity getBiometric() {
        return biometric;
    }

    public void setBiometric(@Nullable BiometricEntity biometric) {
        this.biometric = biometric;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", company=" + company +
                ", companyClientId='" + companyClientId + '\'' +
                ", templateId='" + templateId + '\'' +
                ", template=" + template +
                ", validTo=" + validTo +
                ", validFrom=" + validFrom +
                ", status=" + status +
                ", createdByIp='" + createdByIp + '\'' +
                ", verifiedByIp='" + verifiedByIp + '\'' +
                ", created=" + created +
                ", updatedAt=" + updatedAt +
                ", acceptHistoryMessage='" + acceptHistoryMessage + '\'' +
                ", rejectHistoryMessage='" + rejectHistoryMessage + '\'' +
                ", expiredHistoryMessage='" + expiredHistoryMessage + '\'' +
                ", failedBiometricHistoryMessage='" + failedBiometricHistoryMessage + '\'' +
                ", request=" + request +
                ", isViewed=" + isViewed +
                ", signature=" + signature +
                ", biometric=" + biometric +
                '}';
    }
}
