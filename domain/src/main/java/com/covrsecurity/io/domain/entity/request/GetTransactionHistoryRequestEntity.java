package com.covrsecurity.io.domain.entity.request;

public class GetTransactionHistoryRequestEntity {

    private int pageNumber;
    private int pageSize;

    public GetTransactionHistoryRequestEntity(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
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
        return "GetTransactionHistoryRequestEntity{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}
