package com.covrsecurity.io.model;

import android.util.Log;

import java.util.Date;

public class QrCodeStringJson {
    private String reference_id;
    private long expires_at;
    private String type;
    private String status;
    private String scopes;

    public QrCodeStringJson(String reference_id, long expires_at, String type, String status, String scopes) {
        this.reference_id = reference_id;
        this.expires_at = expires_at;
        this.type = type;
        this.status = status;
        this.scopes = scopes;
    }

    public String getReference_id() {
        return reference_id;
    }

    public long getExpires_at() {
        return expires_at;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("QrCodeStringJson{")
                .append("reference_id:").append(reference_id)
                .append(", expires_at:").append(expires_at)
                .append(", type:").append(type)
                .append(", status:").append(status)
                .append(", scopes:").append(scopes)
                .append("}").toString();
    }
}
