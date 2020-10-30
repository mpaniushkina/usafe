package com.covrsecurity.io.model;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.IamApp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Egor on 29.08.2016.
 */

public class RegistrationPush {

    public static final String STATUS_QUEUED = "queued";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_SENT = "sent";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_UNDELIVERED = "undelivered";

    private static final Map<String, Integer> STATUS_MESSAGES;

    static {
        STATUS_MESSAGES = new HashMap<>(2);
        STATUS_MESSAGES.put(STATUS_SENT, R.string.status_sent_sms);
        STATUS_MESSAGES.put(STATUS_DELIVERED, R.string.status_delivered_to_inbox);
        STATUS_MESSAGES.put(STATUS_FAILED, R.string.status_fail_to_sent_sms);
        STATUS_MESSAGES.put(STATUS_UNDELIVERED, R.string.status_fail_to_sent_sms);
    }

    private String status;

    public RegistrationPush(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusMessage() {
        Integer statusMessageStringResId = STATUS_MESSAGES.get(status);
        if (statusMessageStringResId != null) {
            return IamApp.getInstance().getString(statusMessageStringResId);
        } else {
            return status;
        }
    }

    public boolean isSuccessfulStatus() {
        return !STATUS_FAILED.equals(status) && !STATUS_UNDELIVERED.equals(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RegistrationPush{" +
                "status='" + status + '\'' +
                '}';
    }
}