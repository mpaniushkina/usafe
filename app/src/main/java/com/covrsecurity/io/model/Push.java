package com.covrsecurity.io.model;

import java.io.Serializable;

/**
 * Created by alex on 6.5.16.
 */
public class Push implements Serializable {

    private String alert;
    private int badge;

    public Push(String alert, int badge) {
        this.alert = alert;
        this.badge = badge;
    }

    public String getAlert() {
        return alert;
    }

    public int getBadge() {
        return badge;
    }

    @Override
    public String toString() {
        return "Push{" +
                "alert='" + alert + '\'' +
                ", badge=" + badge +
                '}';
    }
}
