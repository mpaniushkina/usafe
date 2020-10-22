package com.covrsecurity.io.domain.entity.response;

import com.covrsecurity.io.domain.entity.MerchantEntity;

import java.util.List;

public class GetConnectionsResponseEntity {

    private List<MerchantEntity> merchants = null;
    private int pageSize;
    private int pageNumber;
    private boolean hasNext;

    public GetConnectionsResponseEntity(List<MerchantEntity> merchants, int pageSize, int pageNumber, boolean hasNext) {
        this.merchants = merchants;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.hasNext = hasNext;
    }

    public List<MerchantEntity> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<MerchantEntity> merchants) {
        this.merchants = merchants;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return "GetConnectionsResponseEntity{" +
                "merchants=" + merchants +
                ", pageSize=" + pageSize +
                ", pageNumber=" + pageNumber +
                ", hasNext=" + hasNext +
                '}';
    }
}
