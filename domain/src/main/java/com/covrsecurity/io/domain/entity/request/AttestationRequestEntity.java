package com.covrsecurity.io.domain.entity.request;

public class AttestationRequestEntity {

    private String statement;
    private String deviceId;

    public AttestationRequestEntity(String statement, String deviceId) {
        this.statement = statement;
        this.deviceId = deviceId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "AttestationRequestEntity{" +
                "statement='" + statement + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
