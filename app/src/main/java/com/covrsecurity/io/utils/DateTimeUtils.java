package com.covrsecurity.io.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

/**
 * Created by Lenovo on 20.01.2017.
 */

public class DateTimeUtils {

    private static final String TIME_FORMAT = "MMM dd, yyyy. HH:mm";

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern(TIME_FORMAT)
            .withZone(DateTimeZone.forTimeZone(TimeZone.getDefault()));

    public static String getFormattedTime(DateTime  dateTime) {
        return formatter.print(dateTime);
    }

}