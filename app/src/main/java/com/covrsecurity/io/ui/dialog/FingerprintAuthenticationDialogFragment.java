package com.covrsecurity.io.ui.dialog;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.covrsecurity.io.R;
import com.covrsecurity.io.model.error.FingerprintRecognitionError;
import com.covrsecurity.io.ui.interfaces.IFingerprintAuthCallBack;
import com.covrsecurity.io.utils.FingerprintUiHelper;
import com.covrsecurity.io.utils.FingerprintUtils;

import javax.crypto.Cipher;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintAuthenticationDialogFragment extends SafeDismissDialogFragment implements
        IFingerprintAuthCallBack {

    public static final String DEFAULT_KEY_NAME = "default_key";

    private static final String DIALOG_SCANNING_TAG = "com.covrsecurity.io.ui.dialog.FingerprintAuthenticationDialogFragment.DIALOG_SCANNING_TAG";
    private static final String TITLE_EXTRA = "TITLE_EXTRA";

    public static FingerprintAuthenticationDialogFragment getInstance(String title) {
        FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_EXTRA, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static FingerprintAuthenticationDialogFragment getInstance() {
        return new FingerprintAuthenticationDialogFragment();
    }

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private IFingerprintAuthCallBack mCallback;
    private String mTitle;
    private boolean mEnrolledFingersDidNotChange;

    @Override
    protected String provideTag() {
        return DIALOG_SCANNING_TAG;
    }

    public boolean isEnrolledFingersDidNotChange() {
        return mEnrolledFingersDidNotChange;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE_EXTRA);
        }
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fingerprint_container, container, false);
        TextView title = v.findViewById(R.id.fingerprint_title);
        v.findViewById(R.id.cancel_button).setOnClickListener(view -> {
            dismiss();
            mCallback.onError(new FingerprintRecognitionError(FingerprintRecognitionError.CANCEL));
//            AppAdapter.settings().setKeyFingerprintAuthTooManyAttempts(true);
        });
        if (!TextUtils.isEmpty(mTitle)) {
            title.setText(mTitle);
        }

        mFingerprintUiHelper = new FingerprintUiHelper(
                getActivity().getSystemService(FingerprintManager.class),
                v.findViewById(R.id.fingerprint_icon),
                v.findViewById(R.id.fingerprint_status), this);

        FingerprintManager fingerprintManager = FingerprintUtils.getInstance(getActivity()).getFingerprintManager();
        CancellationSignal signal = new CancellationSignal();
        fingerprintManager.authenticate(mCryptoObject, signal, 0, mFingerprintUiHelper, null);

        return v;
    }

    public void initCryptoObject(int cipherMode, @Nullable byte[] ivBytes) {
        FingerprintUtils.getInstance(getActivity()).generateKey(DEFAULT_KEY_NAME, true, cipherMode);
        try {
            Cipher cipher = FingerprintUtils.getInstance(getActivity()).generateCipher(cipherMode, ivBytes);
            mCryptoObject = new FingerprintManager.CryptoObject(cipher);
            mEnrolledFingersDidNotChange = true;
        } catch (Exception e) {
            mEnrolledFingersDidNotChange = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening(mCryptoObject);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    public void setCallback(IFingerprintAuthCallBack callback) {
        mCallback = callback;
    }

    @Override
    public void onAuthenticated(Cipher cipher) {
        if (mCallback != null) {
            mCallback.onAuthenticated(cipher);
        }
        dismiss();
    }

    @Override
    public void onError(FingerprintRecognitionError error) {
        if (mCallback != null) {
            mCallback.onError(error);
        }
        dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (mCryptoObject == null) {
            throw new IllegalStateException("You should call initCryptoObject() before calling show()");
        }
        super.show(manager, tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        if (mCryptoObject == null) {
            throw new IllegalStateException("You should call initCryptoObject() before calling show()");
        }
        return super.show(transaction, tag);
    }
}