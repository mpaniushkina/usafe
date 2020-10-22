package com.covrsecurity.io.domain.entity.request;

public class NotificationHubRegistrationRequestEntity {

    private String notificationKey;

    public NotificationHubRegistrationRequestEntity(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    @Override
    public String toString() {
        return "NotificationHubRegistrationRequestEntity{" +
                "notificationKey='" + notificationKey + '\'' +
                '}';
    }
}
