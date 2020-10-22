package com.covrsecurity.io.domain.entity.response;

import com.covrsecurity.io.domain.entity.TransactionEntity;

public class GetPushNotificationsResponseEntity {

    private TransactionEntity transaction;
    private String lockToken;

    public GetPushNotificationsResponseEntity(TransactionEntity transaction, String lockToken) {
        this.transaction = transaction;
        this.lockToken = lockToken;
    }

    public TransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntity transaction) {
        this.transaction = transaction;
    }

    public String getLockToken() {
        return lockToken;
    }

    public void setLockToken(String lockToken) {
        this.lockToken = lockToken;
    }

    @Override
    public String toString() {
        return "GetPushNotificationsResponseEntity{" +
                "transaction=" + transaction +
                ", lockToken='" + lockToken + '\'' +
                '}';
    }
}
