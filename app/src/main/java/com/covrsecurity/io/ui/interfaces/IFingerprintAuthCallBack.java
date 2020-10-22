package com.covrsecurity.io.ui.interfaces;

import com.covrsecurity.io.model.error.FingerprintRecognitionError;

import javax.crypto.Cipher;

public interface IFingerprintAuthCallBack {

    void onAuthenticated(Cipher cipher);

    void onError(FingerprintRecognitionError error);
}