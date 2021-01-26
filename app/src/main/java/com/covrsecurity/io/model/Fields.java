package com.covrsecurity.io.model;

import java.util.UUID;

public final class Fields {

    public static final String MESSAGE = "message";
    public static final String TITLE = "title";
    public static final String PENDING_REQUESTS = "pendingRequests";
    public static final String EXPIRATIONS = "expirations";

    public static final String TOKEN_EXTRA = "TOKEN_EXTRA";
    public static final String PHONE_EXTRA = "PHONE_EXTRA";
    public static final String VERIFICATION_TYPE_EXTRA = "VERIFICATION_TYPE_EXTRA";

    public static final String COVR_SDK_ID = "userRegistrationSdk";
    public static final String COVR_SDK_SECRET = "secret";

    //TODO new keys
    public static final String COVR_SDK_QRCODE_ID = "d23857eb-c1c1-4b5b-bcc1-d2ec58d131f5";
    public static final String COVR_SDK_QRCODE_SECRET = "2ccd4da0-795b-4e07-b6b7-78750dd91d5a";
    public static final String GET_QRCODE_COMPANY_USER_ID = UUID.randomUUID().toString();
    public static final String GET_QRCODE_TRANSACTION_ID = UUID.randomUUID().toString();

}
