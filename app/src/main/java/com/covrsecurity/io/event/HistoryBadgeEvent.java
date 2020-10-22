package com.covrsecurity.io.event;

/**
 * Created by alex on 13.5.16.
 */
public class HistoryBadgeEvent {

    private int mBadgeCount = 0;

    public HistoryBadgeEvent(int badgeCount) {
        mBadgeCount = badgeCount;
    }

    public int getBadgeCount() {
        return mBadgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        mBadgeCount = badgeCount;
    }
}
