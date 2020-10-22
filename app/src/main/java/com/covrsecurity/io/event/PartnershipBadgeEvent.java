package com.covrsecurity.io.event;

/**
 * Created by alex on 13.5.16.
 */
public class PartnershipBadgeEvent {

    private int mPartnershipBadgeCount;

    public PartnershipBadgeEvent(int partnershipBadgeCount) {
        mPartnershipBadgeCount = partnershipBadgeCount;
    }

    public int getPartnershipBadgeCount() {
        return mPartnershipBadgeCount;
    }

    public void setPartnershipBadgeCount(int partnershipBadgeCount) {
        mPartnershipBadgeCount = partnershipBadgeCount;
    }
}
