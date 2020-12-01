package com.covrsecurity.io.domain.entity.request;

public class QrCodeClaimRequestEntity {
    private String qrCodeString;

    public QrCodeClaimRequestEntity(String qrCodeString) {
        this.qrCodeString = qrCodeString;
    }

    public String getQrCodeString() {
        return qrCodeString;
    }

    public void setQrCodeString(String qrCodeString) {
        this.qrCodeString = qrCodeString;
    }

    @Override
    public String toString() {
        return "PostQrCodeRequestEntity{" +
                "qrCodeString='" + qrCodeString + '\'' +
                '}';
    }
}
