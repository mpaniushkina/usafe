package com.covrsecurity.io.domain.entity.response;

public class QrCodeConnectionResponseEntity {

    private String qrCode;
    private String qrCodeString;

    public QrCodeConnectionResponseEntity(String qrCode, String qrCodeString) {
        this.qrCode = qrCode;
        this.qrCodeString = qrCodeString;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getQrCodeString() {
        return qrCodeString;
    }

    public void setQrCodeString(String qrCodeString) {
        this.qrCodeString = qrCodeString;
    }

    @Override
    public String toString() {
        return "QrCodeConnectionResponseEntity{" +
                "qrCode='" + qrCode + '\'' +
                ", qrCodeString='" + qrCodeString + '\'' +
                '}';
    }
}
