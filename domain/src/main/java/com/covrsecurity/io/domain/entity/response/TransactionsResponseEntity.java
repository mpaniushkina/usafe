package com.covrsecurity.io.domain.entity.response;

import com.covrsecurity.io.domain.entity.TransactionEntity;

import java.util.List;

public class TransactionsResponseEntity {

    private List<TransactionEntity> transactions;
    private boolean hasNext;
    private int pageNumber;
    private int pageSize;

    public TransactionsResponseEntity(List<TransactionEntity> transactions, boolean hasNext, int pageNumber, int pageSize) {
        this.transactions = transactions;
        this.hasNext = hasNext;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "TransactionsResponseEntity{" +
                "transactions=" + transactions +
                ", hasNext=" + hasNext +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}
