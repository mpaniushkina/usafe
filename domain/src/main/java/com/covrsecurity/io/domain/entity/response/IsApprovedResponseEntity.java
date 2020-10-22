package com.covrsecurity.io.domain.entity.response;

import java.util.List;

public class IsApprovedResponseEntity {

    private List<Integer> platform;

    public IsApprovedResponseEntity(List<Integer> platform) {
        this.platform = platform;
    }

    public List<Integer> getPlatform() {
        return platform;
    }

    public void setPlatform(List<Integer> platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "IsApprovedResponseEntity{" +
                "platform=" + platform +
                '}';
    }
}
