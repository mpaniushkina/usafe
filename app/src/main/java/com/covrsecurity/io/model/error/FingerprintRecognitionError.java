package com.covrsecurity.io.model.error;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

public class FingerprintRecognitionError {
    @Cause
    private int mCause;
    @Nullable
    private String mMessage;

    public FingerprintRecognitionError(int cause) {
        mCause = cause;
    }

    public FingerprintRecognitionError(int cause, @Nullable String message) {
       mCause = cause;
       mMessage = message;
    }

    public int getCause() {
        return mCause;
    }

    @Nullable
    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        return "FingerprintRecognitionError{" +
                "mCause=" + mCause +
                ", mMessage='" + mMessage + '\'' +
                '}';
    }

    public static final int CANCEL = 1;
    public static final int MAX_ATTEMPTS = 2;
    public static final int ERROR = 3;

    @IntDef({CANCEL, MAX_ATTEMPTS, ERROR})
    @interface Cause{
    }

}