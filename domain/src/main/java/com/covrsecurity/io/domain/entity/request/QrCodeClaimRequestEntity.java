package com.covrsecurity.io.domain.entity.request;

public class QrCodeClaimRequestEntity {
    private String reference_id;
    private int expires_at;
    private String type;
    private String status;
    private String scopes;

    public QrCodeClaimRequestEntity(String reference_id, int expires_at, String type, String status, String scopes) {
        this.reference_id = reference_id;
        this.expires_at = expires_at;
        this.type = type;
        this.status = status;
        this.scopes = scopes;
    }

    public String getReference_id() {
        return reference_id;
    }

    public int getExpires_at() {
        return expires_at;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getScopes() {
        return scopes;
    }

    @Override
    public String toString() {
        return "QrCodeClaimRequestEntity{" +
                "reference_id='" + reference_id + '\'' +
                "expires_at='" + expires_at + '\'' +
                "type='" + type + '\'' +
                "status='" + status + '\'' +
                "scopes='" + scopes + '\'' +
                '}';
    }
}
