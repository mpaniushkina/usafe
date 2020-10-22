package com.covrsecurity.io.domain.entity;

import java.io.Serializable;

public class RequestEntity implements Serializable {

    private String title;
    private String message;
    private String accept;
    private String reject;

    public RequestEntity(String title, String message, String accept, String reject) {
        this.title = title;
        this.message = message;
        this.accept = accept;
        this.reject = reject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getReject() {
        return reject;
    }

    public void setReject(String reject) {
        this.reject = reject;
    }

    @Override
    public String toString() {
        return "RequestEntity{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", accept='" + accept + '\'' +
                ", reject='" + reject + '\'' +
                '}';
    }
}
