package com.covrsecurity.io.data.repository;

import com.covrsecurity.io.data.utils.DataConstants;
import com.covrsecurity.io.common.ConstantsUtils;
import com.covrsecurity.io.domain.repository.TrueTimeRepository;
import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.util.Date;

import io.reactivex.Single;

public class TrueTimeRepositoryImpl implements TrueTimeRepository {

    @Override
    public Single<Date> initializeTrueTime(int retryCount) {
        return Single.fromCallable(this::initializeTrueTime)
                .retry(retryCount);
    }

    private Date initializeTrueTime() throws IOException {
        TrueTime.build()
                .withNtpHost(DataConstants.NTP_HOST)
                .withServerResponseDelayMax(ConstantsUtils.MILLISECONDS_IN_SECOND)
                .withRootDelayMax(ConstantsUtils.MILLISECONDS_IN_SECOND)
                .initialize();
        return TrueTime.now();
    }
}
