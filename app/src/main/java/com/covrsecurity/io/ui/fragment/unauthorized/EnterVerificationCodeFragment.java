package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentEnterVerificationCodeBinding;
import com.covrsecurity.io.event.VerificationCodeReceivedEvent;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.model.Fields;
import com.covrsecurity.io.model.RegistrationPush;
import com.covrsecurity.io.model.databinding.EnterVerificationCodeModel;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.interfaces.IKeyboardListener;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.ScreenUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.disposables.Disposable;

public class EnterVerificationCodeFragment extends BaseUnauthorizedFragment<FragmentEnterVerificationCodeBinding>
        implements IKeyboardListener {

    private static final int MAX_CODE_LENGTH = 4;
    private static final int TRY_AGAIN_DELAY = 40000; // number of miliseconds to activate "try again" link after

    private EnterVerificationCodeModel enterVerificationModel;
    private String mToken;
    private String mPhone;
    private Disposable mDisposable;

    public static Fragment newInstance(String token, String phone) {
        Fragment fragment = new EnterVerificationCodeFragment();
        Bundle args = new Bundle();
        args.putString(Fields.TOKEN_EXTRA, token);
        args.putString(Fields.PHONE_EXTRA, phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        AppAdapter.bus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppAdapter.bus().unregister(this);
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mToken = arguments.getString(Fields.TOKEN_EXTRA);
            mPhone = arguments.getString(Fields.PHONE_EXTRA);
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        defineSpace();
        if (enterVerificationModel == null) {
            enterVerificationModel = new EnterVerificationCodeModel(MAX_CODE_LENGTH);
        }
        mBinding.setEnterVerificationModel(enterVerificationModel);
        mBinding.digitalKeyboard.setKeyboardListener(this);
        enableNextButton(enterVerificationModel.getDigitsNumber() == MAX_CODE_LENGTH);
        mBinding.deleteButton.setOnClickListener((View deleteButton) -> removeDigit());
        SpannableString spannableString = new SpannableString(getString(R.string.verification_explanation));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_setup_read_more_verification_code));
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString
                .setSpan(clickableSpan, spannableString.length() - getString(R.string.phone_number_read_more).length(),
                        spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!enterVerificationModel.isRetryButtonEnabled()) {
            ActivityUtils.scheduleOnMainThread(() -> {
                enterVerificationModel.setRetryButtonEnabled(true);
            }, TRY_AGAIN_DELAY);
        }
        mBinding.tvTryAgain.setOnClickListener((View tryAgainBtn) -> {
            Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_PHONE_REENTER);
            ((UnauthorizedActivity) getActivity()).goBack();
        });
        mBinding.tvPhoneSubheader.setText(spannableString);
        mBinding.tvPhoneSubheader.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPushReceived(RegistrationPush push) {
        if (push != null) {
            AppAdapter.bus().removeStickyEvent(push);
            Log.e("onPushReceived", "PUSH - UnauthorizedActivity with body = " + push.toString());
            enterVerificationModel.setStatus(push);
            if (RegistrationPush.STATUS_UNDELIVERED.equals(push.getStatus())
                    || RegistrationPush.STATUS_FAILED.equals(push.getStatus())) {
                enterVerificationModel.setRetryButtonEnabled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onVerificationCodeReceived(VerificationCodeReceivedEvent event) {
        if (event != null) {
            AppAdapter.bus().removeStickyEvent(event);
            for (char c : event.getCode().toCharArray()) {
                addDigit(c);
            }
            onNextButtonClick();
        }
    }

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_enter_verification_code;
    }

    @Override
    protected void onNextButtonClick() {
        super.onNextButtonClick();
        /*showProgress();
        String pin = enterVerificationModel.digitsToString();
        CheckVerificationCodeRequest request = new CheckVerificationCodeRequest(RegisterType.TYPE_SMS,
                Constants.FIELD_ANDROID, mPhone, mToken, pin, DeviceIDUtils.getUniquePsuedoID());
        mDisposable = AppAdapter.getRegistrationApi().checkVerificationCode(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(this::hideProgress)
                .subscribe(registerResponse -> {
                            hideProgress();
                            if (registerResponse.getIsVerificates()) {
                                Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_PIN_SUCCESS);
                                Fragment fragment = InfoEnterCodeFragment.newInstance(
                                        RegisterType.TYPE_SMS ,mToken, mPhone);
                                replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
                            } else {
                                Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_PIN_FAILURE);
                                DialogUtils.showOkDialog(getActivity(), getString(R.string.verification_icorrect_title),
                                        getString(R.string.verification_icorrect_text), null, null, true);
                                Timber.d("incorrect pin code");
                            }
                        },
                        throwable -> {
                            Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_PIN_ERROR);
                            Timber.e("pin verification failed");
                            Timber.e(throwable);
                            hideProgress();
                        });*/
    }

    @Override
    public void onKeyboardButtonClick(boolean clickNotInterceded, char value) {
        if (clickNotInterceded) {
            addDigit(value);
        } else {
            showToast(R.string.touch_been_intercepted_alert);
        }
    }

    private void addDigit(char value) {
        enterVerificationModel.addDigit(value);
        if (enterVerificationModel.getDigitsNumber() == MAX_CODE_LENGTH) {
            enableNextButton(true);
        }
    }

    private void removeDigit() {
        enterVerificationModel.remoteDigit();
        if (enterVerificationModel.getDigitsNumber() < MAX_CODE_LENGTH) {
            enableNextButton(false);
        }
    }

    private void defineSpace() {
        if (ScreenUtils.hasNavBar()) {
            mBinding.statusTrySpace.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            AppAdapter.resources().getDimensionPixelSize(R.dimen.enter_verification_view_padding) / 2));
            mBinding.statusTrySpace.invalidate();
        }
    }

}
