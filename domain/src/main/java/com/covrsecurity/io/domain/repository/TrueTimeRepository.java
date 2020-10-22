package com.covrsecurity.io.domain.repository;

import java.util.Date;

import io.reactivex.Single;

public interface TrueTimeRepository {

    Single<Date> initializeTrueTime(int retryCount);
}
