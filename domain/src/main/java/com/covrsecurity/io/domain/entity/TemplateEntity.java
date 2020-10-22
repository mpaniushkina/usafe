package com.covrsecurity.io.domain.entity;

import java.io.Serializable;

public class TemplateEntity implements Serializable {

    private int layoutType;
    private String backgroundImage;
    private String TransactionImage;

    public TemplateEntity(int layoutType, String backgroundImage, String transactionImage) {
        this.layoutType = layoutType;
        this.backgroundImage = backgroundImage;
        TransactionImage = transactionImage;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getTransactionImage() {
        return TransactionImage;
    }

    public void setTransactionImage(String transactionImage) {
        TransactionImage = transactionImage;
    }

    @Override
    public String toString() {
        return "TemplateEntity{" +
                "layoutType=" + layoutType +
                ", backgroundImage='" + backgroundImage + '\'' +
                ", TransactionImage='" + TransactionImage + '\'' +
                '}';
    }
}
