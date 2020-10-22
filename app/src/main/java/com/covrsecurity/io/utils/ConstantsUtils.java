package com.covrsecurity.io.utils;

import android.Manifest;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ConstantsUtils {

    public static final String INTENT_KEY_FOR_TUTORIAL_FRAGMENT = "tutorial";

    // time
    public static final int TEN_MILLISECONDS = 10;
    public static final int TWO_HUNDRED_MILLISECONDS = 200;
    public static final int THREE_HUNDRED_MILLISECONDS = 300;
    public static final int FOUR_HUNDRED_MILLISECONDS = 400;
    public static final int HALF_SECOND = 500;
    public static final int SIX_HUNDRED_MILLISECONDS = 600;
    public static final int MILLISECONDS_IN_SECOND = 1000;
    public static final long MINUTES_IN_SECOND = 60L;

    public static final String POSITIVE_BUTTON_TEXT_YES = "yes";
    public static final String POSITIVE_BUTTON_TEXT_OK = "ok";

    public static final String COVR_DIRECTORY_TEMPLATE = "%s" + File.separator + "CovrDirectory";

    public static final String FORMAT_DATE_NORMAL_STR = "dd.MM.yyyy HH:mm";
    public static final String FORMAT_DATE_HH_MM_SS_SRT = "HH:mm:ss";

    public static final DateFormat FORMAT_DATE_NORMAL1 = new SimpleDateFormat(FORMAT_DATE_NORMAL_STR);

    public static final char DEFAULT_ITEM_PLACEHOLDER = '#';
    public static final String DEFAULT_EXPIRE_DATE_PATTERN = "##/##";

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_MERCHANT_PAGE_SIZE = 10;
    public static final int DEFAULT_TRANSACTIONS_PAGE_SIZE = 100;
    public static final int DEFAULT_HISTORY_PAGE_SIZE = 20;

    public static final int CAMERA_REQUEST_CODE = 2001;
    public static final int CREATE_SMS_TOKEN_REQUEST_CODE = 2002;
    public static final int CALL_PHONE_REQUEST_CODE = 2003;
    public static final int SCAN_QR_REQUEST_CODE = 2004;

    public static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    public static final String APP_SETTINGS_TEMPLATE = "package:%s";

    public static final Type PENDING_REQUESTS_TYPE = TypeToken.getParameterized(ArrayList.class, Long.class).getType();
}

