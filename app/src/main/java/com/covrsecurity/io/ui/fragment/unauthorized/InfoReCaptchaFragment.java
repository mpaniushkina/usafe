package com.covrsecurity.io.ui.fragment.unauthorized;
/*

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentRecaptchaBinding;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.network.model.RegisterType;
import com.covrsecurity.io.network.request.registration.CheckVerificationCodeRequest;
import com.covrsecurity.io.network.utils.Constants;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.DeviceIDUtils;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetStatusCodes;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class InfoReCaptchaFragment extends BaseUnauthorizedFragment<FragmentRecaptchaBinding> {

    private Disposable mDisposable;

    public static Fragment newInstance() {
        return new InfoReCaptchaFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recaptcha;
    }

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.infoText.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString spannableString = new SpannableString(getString(R.string.textview_recaptcha_message));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String termsOfUseURL = (BuildConfig.DEBUG) ?
                        getString(R.string.cfg_about_terms_of_use_dev) :
                        getString(R.string.cfg_about_terms_of_use);

                Fragment f = ReadMorePhoneFragment.newInstance(termsOfUseURL);
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString
                .setSpan(clickableSpan, spannableString.length() - getString(R.string.here).length(),
                        spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.infoText.setText(spannableString);
    }

    @Override
    protected void onNextButtonClick() {
        super.onNextButtonClick();

        showProgress();
        Activity activity = getActivity();
        if (activity != null) {
            String siteKey = (BuildConfig.DEBUG) ? ConstantsUtils.RECAPTCHA_SITE_KEY_DEV :
                    ConstantsUtils.RECAPTCHA_SITE_KEY_PROD;
            SafetyNet.getClient(activity).verifyWithRecaptcha(
                    siteKey)
                    .addOnSuccessListener(recaptchaTokenResponse ->
                            checkToken(recaptchaTokenResponse.getTokenResult()))
                    .addOnFailureListener(
                        error -> {
                            if (error instanceof ApiException) {
                                ApiException apiException = (ApiException) error;
                                int statusCode = apiException.getStatusCode();
                                switch (statusCode) {
                                    case SafetyNetStatusCodes.RECAPTCHA_INVALID_SITEKEY:
                                    case SafetyNetStatusCodes.RECAPTCHA_INVALID_KEYTYPE:
                                    case SafetyNetStatusCodes.RECAPTCHA_INVALID_PACKAGE_NAME:
                                        throw new RuntimeException("Recaptcha problem. Check " +
                                                "your sitekey or package name in the reCaptcha " +
                                                "Admin console.");
                                    case SafetyNetStatusCodes.UNSUPPORTED_SDK_VERSION:
                                        throw new RuntimeException("Recaptcha problem. SDK " +
                                                "version is not supported");
                                    case SafetyNetStatusCodes.TIMEOUT:
                                    case SafetyNetStatusCodes.NETWORK_ERROR:
                                    case SafetyNetStatusCodes.ERROR:
                                        hideProgress();
                                        ((UnauthorizedActivity)activity).showNoInternetDialog();
                                        break;

                                }
                            } else {
                                hideProgress();
                                ((UnauthorizedActivity)activity).showUnknownErrorDialog(
                                        error.getMessage());
                            }
                        });
        }
    }

    private void checkToken(String token) {
        CheckVerificationCodeRequest request = new CheckVerificationCodeRequest(
                RegisterType.TYPE_RECAPTCHA, Constants.FIELD_ANDROID,"", token, "",
                DeviceIDUtils.getUniquePsuedoID());

        mDisposable = AppAdapter.getRegistrationApi().checkVerificationCode(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(this::hideProgress)
                .subscribe(registerResponse -> {
                            hideProgress();
                            if (registerResponse.getIsVerificates()) {
                                Fragment fragment = InfoEnterCodeFragment.newInstance(
                                        RegisterType.TYPE_RECAPTCHA, token, "");
                                replaceFragment(fragment, fragment.getArguments(), true,
                                        FragmentAnimationSet.SLIDE_LEFT);
                            } else {
                                Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_PIN_FAILURE);
                                DialogUtils.showOkDialog(getActivity(),
                                        getString(R.string.verification_icorrect_title),
                                        getString(R.string.verification_icorrect_captcha_text),
                                        null, null, true);
                                Timber.d("");
                            }
                        },
                        throwable -> {
                            Timber.e("Recaptcha failed");
                            Timber.e(throwable);
                            hideProgress();
                        });
    }

    @Override
    public void onResume() {
        super.onResume();
        enableNextButton(true);

        Activity activity = getActivity();
        if (activity != null) {
            ((UnauthorizedActivity) activity).getRightButton().setOnClickListener((v) ->
                    onNextButtonClick());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}*/
