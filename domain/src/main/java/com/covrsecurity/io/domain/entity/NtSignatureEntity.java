package com.covrsecurity.io.domain.entity;

import java.io.Serializable;

public class NtSignatureEntity implements Serializable {

    private String companySignature;
    private String covrSignature;
    private String clientSignature;
    private String registrationSignature;

    public NtSignatureEntity(String companySignature, String covrSignature, String clientSignature, String registrationSignature) {
        this.companySignature = companySignature;
        this.covrSignature = covrSignature;
        this.clientSignature = clientSignature;
        this.registrationSignature = registrationSignature;
    }

    public String getCompanySignature() {
        return companySignature;
    }

    public void setCompanySignature(String companySignature) {
        this.companySignature = companySignature;
    }

    public String getCovrSignature() {
        return covrSignature;
    }

    public void setCovrSignature(String covrSignature) {
        this.covrSignature = covrSignature;
    }

    public String getClientSignature() {
        return clientSignature;
    }

    public void setClientSignature(String clientSignature) {
        this.clientSignature = clientSignature;
    }

    public String getRegistrationSignature() {
        return registrationSignature;
    }

    public void setRegistrationSignature(String registrationSignature) {
        this.registrationSignature = registrationSignature;
    }

    @Override
    public String toString() {
        return "NtSignatureEntity{" +
                "companySignature='" + companySignature + '\'' +
                ", covrSignature='" + covrSignature + '\'' +
                ", clientSignature='" + clientSignature + '\'' +
                ", registrationSignature='" + registrationSignature + '\'' +
                '}';
    }
}
