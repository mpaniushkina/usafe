package com.covrsecurity.io.utils;

import com.instacart.library.truetime.TrueTimeRx;

import java.util.Date;

import io.reactivex.Single;

import static com.covrsecurity.io.data.utils.DataConstants.NTP_HOST;

public final class TrueTimeUtils {

    private TrueTimeUtils() {
    }

    public static Single<Date> initializeTrueTime() {
        return TrueTimeRx.build()
                .withServerResponseDelayMax(ConstantsUtils.MILLISECONDS_IN_SECOND)
                .withRootDelayMax(ConstantsUtils.MILLISECONDS_IN_SECOND)
                .initializeRx(NTP_HOST);

    }
}
