package com.covrsecurity.io.domain.entity.response;

public class GetUnreadHistoryCountResponseEntity {

    private int count;

    public GetUnreadHistoryCountResponseEntity(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "GetUnreadHistoryCountResponseEntity{" +
                "count=" + count +
                '}';
    }
}
