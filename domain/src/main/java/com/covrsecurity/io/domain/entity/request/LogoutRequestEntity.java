package com.covrsecurity.io.domain.entity.request;

public class LogoutRequestEntity {

    private final String id;
    private final String secret;
    private final String accessToken;

    public LogoutRequestEntity(String id, String secret, String accessToken) {
        this.id = id;
        this.secret = secret;
        this.accessToken = accessToken;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "LogoutRequestEntity{" +
                "id='" + id + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}
