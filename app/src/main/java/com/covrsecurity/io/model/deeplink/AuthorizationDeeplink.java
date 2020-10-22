package com.covrsecurity.io.model.deeplink;

import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.Serializable;

import timber.log.Timber;

/**
 * Created by alex on 18.8.17.
 */

public class AuthorizationDeeplink implements Serializable {

    private static final String KEY_DATA = "data";

    private String mData;

    public AuthorizationDeeplink(@NonNull final Uri uri) {
        Timber.d("Deeplink parsing: " + uri.getQuery());
        mData = uri.getQueryParameter(KEY_DATA);
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(mData);
    }

    public String getData() {
        return mData;
    }
}
