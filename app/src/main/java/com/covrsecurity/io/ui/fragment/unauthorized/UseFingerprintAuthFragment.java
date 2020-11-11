package com.covrsecurity.io.ui.fragment.unauthorized;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentUseFingerprintAuthBinding;
import com.covrsecurity.io.model.error.FingerprintRecognitionError;
import com.covrsecurity.io.sdk.utils.ArrayUtils;
import com.covrsecurity.io.ui.dialog.FingerprintAuthenticationDialogFragment;
import com.covrsecurity.io.ui.dialog.FingerprintAuthenticationFailedDialogFragment;
import com.covrsecurity.io.ui.interfaces.IFingerprintAuthCallBack;
import com.covrsecurity.io.utils.FingerprintUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.StringUtils;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Lenovo on 27.12.2016.
 */
public class UseFingerprintAuthFragment extends BaseUnauthorizedFragment<FragmentUseFingerprintAuthBinding> implements IFingerprintAuthCallBack {

    private static final String COVR_CODE_EXTRA = "COVR_CODE_EXTRA";
    private static final String IS_REGISTRATION_EXTRA = "IS_REGISTRATION_EXTRA";
    private static final String DIALOG_SCANNING_TAG = "DIALOG_SCANNING_TAG";
    private static final String DIALOG_FAILED_TAG = "DIALOG_FAILED_TAG";

    public static UseFingerprintAuthFragment newInstance(char[] covrCode, boolean isRegistration) {
        UseFingerprintAuthFragment fragment = new UseFingerprintAuthFragment();
        Bundle args = new Bundle();
        args.putCharArray(COVR_CODE_EXTRA, covrCode);
        args.putBoolean(IS_REGISTRATION_EXTRA, isRegistration);
        fragment.setArguments(args);
        return fragment;
    }

    public static UseFingerprintAuthFragment newInstance() {
        UseFingerprintAuthFragment fragment = new UseFingerprintAuthFragment();
        return fragment;
    }

    private char[] mCovrCode;
    private boolean mIsRegistration = true;
    private FingerprintAuthenticationDialogFragment mDialogScanning;
    private FingerprintAuthenticationFailedDialogFragment mDialogFailed;
    private boolean isDialogFailedAdded;
    private Disposable mDisposable;

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_use_fingerprint_auth;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCovrCode = bundle.getCharArray(COVR_CODE_EXTRA);
            mIsRegistration = bundle.getBoolean(IS_REGISTRATION_EXTRA);
        }
    }

    @Override
    public boolean isRegistration() {
        return mIsRegistration;
    }

    @Override
    @SuppressLint({"NewApi"})
    @SuppressWarnings({"MissingPermission"})
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        enableNextButton(true);
        boolean canUseFingerprintScanner = FingerprintUtils.getInstance(getContext()).canUseFingerprintScanner(getContext());
        mBinding.useFingerprintGroup.setVisibility(canUseFingerprintScanner ? View.VISIBLE : View.GONE);
        FingerprintUtils instance = FingerprintUtils.getInstance(getContext());
        boolean readyToUseFingerprintScanner = instance.readyToUseFingerprintScanner(getContext());
        mBinding.setUseFingerprint(readyToUseFingerprintScanner);
        String contentString;
        String readMoreString = getString(R.string.read_more);
        if (!readyToUseFingerprintScanner) {
            mBinding.useFingerprintGroup.setVisibility(View.GONE);
            mBinding.title.setText(R.string.fingerprint_auth_available_title);
            contentString = getString(R.string.fingerprint_auth_available_description, readMoreString);
        } else {
            mBinding.useFingerprintGroup.setVisibility(View.VISIBLE);
            mBinding.title.setText(R.string.fingerprint_auth_ready_title);
            contentString = getString(R.string.fingerprint_auth_ready_description, readMoreString);
        }
        mBinding.description.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.description.setText(StringUtils.getClickableSpannable(
                contentString, readMoreString, () -> {
                    Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_about_fingerprint));
                    replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
                }));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onStop() {
        super.onStop();
        if (mDialogScanning != null) {
            mDialogScanning.setCallback(null);
            mDialogScanning.dismiss();
        }
        if (mDialogFailed != null) {
            mDialogFailed.dismiss();
        }
        if (mDisposable != null) {
            mDisposable.dispose();
        }
//        Arrays.fill(mCovrCode, (char) 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onNextButtonClick() {
        super.onNextButtonClick();
        if (FingerprintUtils.getInstance(getActivity()).readyToUseFingerprintScanner(getActivity()) && mBinding.getUseFingerprint() && !isDialogFailedAdded) {
            if (mDialogScanning == null) {
                mDialogScanning = FingerprintAuthenticationDialogFragment.getInstance(getString(R.string.fingerprint_dialog_description_encrypt_with_finger));
                mDialogScanning.initCryptoObject(Cipher.ENCRYPT_MODE, null);
                mDialogScanning.setCallback(this);
            }
            if (mDialogScanning != null && !mDialogScanning.isVisible()) {
                mDialogScanning.show(getFragmentManager(), DIALOG_SCANNING_TAG);
            }
        } else {
            AppAdapter.settings().setUseFingerprintAuth(mBinding.getUseFingerprint());
            replaceFragment(DoneSetupFragment.newInstance(isRegistration()), null, true, FragmentAnimationSet.SLIDE_LEFT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAuthenticated(Cipher cipher) {
        String threadName = Thread.currentThread().getName();
        mDisposable = Single.fromCallable(() -> {
            byte[] bytes = ArrayUtils.charsToBytes(mCovrCode);
            byte[] encryptedBytes = cipher.doFinal(bytes);
            IvParameterSpec ivParams = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
            String iv = Base64.encodeToString(ivParams.getIV(), Base64.NO_WRAP);
            String encrypted = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
            AppAdapter.settings().saveFingerprintPasswordIv(encrypted, iv);
            return iv;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    AppAdapter.settings().setUseFingerprintAuth(mBinding.getUseFingerprint());
                    replaceFragment(DoneSetupFragment.newInstance(isRegistration()), null, true, FragmentAnimationSet.SLIDE_LEFT);
                }, Timber::e);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onError(FingerprintRecognitionError error) {
        if (error.getCause() == FingerprintRecognitionError.MAX_ATTEMPTS) {
            if (mDialogFailed == null) {
                mDialogFailed = FingerprintAuthenticationFailedDialogFragment.getInstance(getString(R.string.fingerprint_dialog_failed_title), getString(R.string.fingerprint_dialog_failed_description_encrypt_with_finger), null);
                mDialogFailed.setDismissListener(() -> {
                    isDialogFailedAdded = false;
                    mBinding.radioNo.setChecked(true);
                    mBinding.radioYes.setEnabled(false);
                });
            }
            if (!mDialogFailed.isVisible() && !mDialogFailed.isAdded() && !isDialogFailedAdded) {
                mDialogFailed.show(getChildFragmentManager(), DIALOG_FAILED_TAG);
                isDialogFailedAdded = true;
            }
        }
    }
}