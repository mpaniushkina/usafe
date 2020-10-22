package com.covrsecurity.io.utils;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.event.PartnershipBadgeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 7.5.16.
 */
public final class AppAdapterUtils {

    public static boolean isPartnershipExists(List<MerchantEntity> partnerships, MerchantEntity partnership) {
        if (partnerships != null && !partnerships.isEmpty()) {
            for (MerchantEntity current : partnerships) {
                if (current.getId().equals(partnership.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isConsumerRequestExists(List<TransactionEntity> consumerRequests, TransactionEntity consumerRequest) {
        if (consumerRequests != null && !consumerRequests.isEmpty()) {
            for (TransactionEntity current : consumerRequests) {
                if (current.getId().equals(consumerRequest.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<TransactionEntity> getNewConsumerRequests(final List<TransactionEntity> oldData,
                                                                 final List<TransactionEntity> newData) {
        List<TransactionEntity> consumerRequests = new ArrayList<>();
        for (TransactionEntity consumerRequest : newData) {
            // TODO: 24.01.2017 use set instead of list
            if (!isConsumerRequestExists(oldData, consumerRequest)) {
                consumerRequests.add(consumerRequest);
            }
        }
        return consumerRequests;
    }

    public static List<MerchantEntity> getNewPartnerships(final List<MerchantEntity> oldData, final List<MerchantEntity> newData) {
        List<MerchantEntity> partnershipList = new ArrayList<>();
        for (MerchantEntity partnership : newData) {
            if (!isPartnershipExists(oldData, partnership)) {
                partnershipList.add(partnership);
            }
        }
        return partnershipList;
    }

    //TODO: UA-32: unviewed partnership are no longer marked with badge, by client request.
    // Some other partnership info change might use it in future
    public static int notifyUnviewedPartnershipsCount(final List<MerchantEntity> partnerships) {
        int newItems = 0;
        if (partnerships != null && !partnerships.isEmpty()) {
            for (MerchantEntity partnership : partnerships) {
                if (!partnership.getCompany().isViewed()) {
                    newItems++;
                }
            }
        }
        AppAdapter.bus().postSticky(new PartnershipBadgeEvent(newItems));
        return newItems;
    }

}
