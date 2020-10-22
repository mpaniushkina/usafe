package com.covrsecurity.io.model.error;

import org.json.JSONException;
import org.json.JSONObject;

public class APIError {

    private Throwable mThrowable;
    private int mErrorCode;
    private String mErrorBody;
    private int mSeverity;
    private String mStatusDescription;
    private String mStackTrace;
    private String mMessage;

    public static final String TAG_MESSAGE = "message";
    public static final String TAG_STATUS_DESCRIPTION = "status-description";
    public static final String TAG_STACK_TRACE = "stackTrace";

    public APIError(final int code, final String errorMessage) {
        this(code, errorMessage, 0);
    }

    public APIError(final int code, final String errorMessage, final int severity) {
        mErrorCode = code;
        mErrorBody = errorMessage;
        mSeverity = severity;
        try {
            if (mErrorBody != null) {
                JSONObject errorBodyJSON = new JSONObject(mErrorBody);
                if (errorBodyJSON.has(TAG_MESSAGE)) {
                    mMessage = errorBodyJSON.getString(TAG_MESSAGE);
                }
                if (errorBodyJSON.has(TAG_STACK_TRACE)) {
                    mStackTrace = errorBodyJSON.getString(TAG_STACK_TRACE);
                }
                if (errorBodyJSON.has(TAG_STATUS_DESCRIPTION)) {
                    mStatusDescription = errorBodyJSON.getString(TAG_STATUS_DESCRIPTION);
                }
            }
        } catch (JSONException ignored) {
        }
    }

    public APIError(Throwable throwable) {
        mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorBody;
    }

    public String getStatusDescription() {
        return mStatusDescription;
    }

    public String getStackTrace() {
        return mStackTrace;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getSeverity() {
        return mSeverity;
    }

    @Override
    public String toString() {
        return "APIError{" +
                "mThrowable=" + mThrowable +
                ", mErrorCode=" + mErrorCode +
                ", mErrorBody='" + mErrorBody + '\'' +
                ", mStatusDescription='" + mStatusDescription + '\'' +
                ", mStackTrace='" + mStackTrace + '\'' +
                ", mMessage='" + mMessage + '\'' +
                '}';
    }
}
