package com.covrsecurity.io.domain.entity.request;

public class PostQrCodeRequestEntity {

    private String qrCodeString;

    public PostQrCodeRequestEntity(String qrCodeString) {
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
