package com.covrsecurity.io.utils;

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.model.error.FingerprintRecognitionError;
import com.covrsecurity.io.ui.interfaces.IFingerprintAuthCallBack;

import javax.crypto.Cipher;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintUiHelper extends FingerprintManager.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;//5000
    private static final long SUCCESS_DELAY_MILLIS = 800;
    private static final long MAX_ATTEMPTS_TIMEOUT = 1000;
    private static final int MAX_ATTEMPTS = 3;

    private final FingerprintManager mFingerprintManager;
    private final ImageView mIcon;
    private final TextView mErrorTextView;
    private final IFingerprintAuthCallBack mCallback;
    private CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;
    private int mAttempts;

    /**
     * Constructor for {@link FingerprintUiHelper}.
     */
    public FingerprintUiHelper(FingerprintManager fingerprintManager, ImageView icon,
                               TextView errorTextView, IFingerprintAuthCallBack callback) {
        mFingerprintManager = fingerprintManager;
        mIcon = icon;
        mErrorTextView = errorTextView;
        mCallback = callback;
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.colorPrimary, null));
        mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.fingerprint_dialog_fingerprint_success));
        final Cipher cipher = result.getCryptoObject().getCipher();
        mIcon.postDelayed(() -> mCallback.onAuthenticated(cipher), SUCCESS_DELAY_MILLIS);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(mIcon.getResources().getString(R.string.fingerprint_dialog_fingerprint_not_recognized), true);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString, false);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        // too many attempts in 30 seco
        if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
            showError(errString, true);
            mIcon.postDelayed(() -> mCallback.onError(new FingerprintRecognitionError(FingerprintRecognitionError.MAX_ATTEMPTS)), MAX_ATTEMPTS_TIMEOUT);
            AppAdapter.settings().setKeyFingerprintAuthTooManyAttempts(true);
//            stopListening();
        }
    }

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        mAttempts = 0;
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        mFingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0 /* flags */, this, null);
        mIcon.setImageResource(R.drawable.ic_fp_40px);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    private void showError(CharSequence errorMsg, boolean isError) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        mErrorTextView.setText(errorMsg);
        mErrorTextView.setTextColor(mErrorTextView.getResources().getColor(R.color.red, null));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
        if (isError) {
            mAttempts++;
        }
        if (mAttempts >= MAX_ATTEMPTS) {
            mIcon.postDelayed(() -> mCallback.onError(new FingerprintRecognitionError(FingerprintRecognitionError.MAX_ATTEMPTS)), MAX_ATTEMPTS_TIMEOUT);
            AppAdapter.settings().setKeyFingerprintAuthTooManyAttempts(true);
//            stopListening();
        }
    }

    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            mErrorTextView.setTextColor(
                    mErrorTextView.getResources().getColor(R.color.faded_tealish, null));
            mErrorTextView.setText(
                    mErrorTextView.getResources().getString(R.string.fingerprint_dialog_hint));
            mIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    };

}