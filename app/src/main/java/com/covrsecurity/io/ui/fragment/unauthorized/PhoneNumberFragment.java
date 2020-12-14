package com.covrsecurity.io.ui.fragment.unauthorized;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentPhoneNumberBinding;
import com.covrsecurity.io.model.Country;
import com.covrsecurity.io.ui.interfaces.IKeyboardListener;
import com.covrsecurity.io.ui.interfaces.SimpleTextChangeListener;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.PhoneNumberUtils;
import com.terrakok.phonematter.PhoneFormat;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class PhoneNumberFragment extends BaseUnauthorizedFragment<FragmentPhoneNumberBinding>
        implements IKeyboardListener {
    private static final int MAX_NUMBER_LENGTH = 15;

    private PhoneFormat mPhoneFormat;
    private Country mCountry;
    private Disposable mDisposable;

    public static Fragment newInstance() {
        return new PhoneNumberFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone_number;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mCountry = PhoneNumberUtils.getDefaultCounty(this.getActivity());
        mPhoneFormat = new PhoneFormat(mCountry.getCountryCode(), getActivity());
        mBinding.setCountryCode(mCountry.getPhoneCode());
        mBinding.setCountryName(mCountry.getCountry());
        mBinding.digitalKeyboard.setKeyboardListener(this);
        SpannableString spannableString = new SpannableString(getString(R.string.phone_number_subheader));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_setup_read_more_phone));
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
        mBinding.setSpannabeText(spannableString);
        mBinding.tvPhoneSubheader.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.setRemovePhoneClickListener(v -> {
            removeLastChar();
        });
        mBinding.etPhoneNumber.addTextChangedListener(new SimpleTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (before < count) {
                    String formatted = mPhoneFormat.format(mCountry.getPhoneCode() + s.toString());
                    mBinding.setPhoneNumber(formatted
                            .substring(mCountry.getPhoneCode().length())
                            .trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void removeLastChar() {
        if (mBinding.getPhoneNumber() != null && mBinding.getPhoneNumber().length() > 0) {
            String normal = mBinding.getPhoneNumber().replaceAll("[^0-9]", "");
            String reducedPhoneNumber = normal.substring(0, normal.length() - 1);
            String formatted = mPhoneFormat.format(mCountry.getPhoneCode() + reducedPhoneNumber.toString());
            mBinding.setPhoneNumber(formatted
                    .substring(mCountry.getPhoneCode().length())
                    .trim());
        }
    }

    private void showSmsSendErrorAlert(Throwable error) {
        Timber.e("APIError : %s", error);
        String message;
        if (!TextUtils.isEmpty(error.getMessage())) {
            message = error.getMessage();
        } else {
            message = getString(R.string.error_sending_sms);
        }
        DialogUtils.showOkDialog(getActivity(), getString(R.string.error_sending_sms_title), message, false);
    }

    @Override
    public void onKeyboardButtonClick(boolean clickNotInterceded, char value) {
        if (clickNotInterceded && mBinding.etPhoneNumber.getText().length() < MAX_NUMBER_LENGTH) {
            mBinding.setPhoneNumber(mBinding.etPhoneNumber.getText().toString() + value);
        } else {
            showToast(R.string.touch_been_intercepted_alert);
        }
    }

    @Override
    public void onBackspaceButtonClick() {
    }

    @Override
    protected void onNextButtonClick() {
        DialogUtils.showAlertDialog(getActivity(), getString(R.string.phone_number_confirmation_one_title),
                getString(R.string.phone_number_confirmation_one, PhoneNumberUtils.normalizePhoneNumber(mCountry.getPhoneCode() + mBinding.getPhoneNumber())),
                getString(R.string.yes),
                (dialog1, which1) -> {
                    // positive button
                    /*showProgress();
                    final String phone = PhoneNumberUtils.normalizePhoneNumber(mCountry.getPhoneCode() + mBinding.getPhoneNumber());
//                    String smsToken = ((UnauthorizedActivity) getActivity()).getSmsToken();
                    SendVerificationCodeRequest request = new SendVerificationCodeRequest(DeviceIDUtils.getUniquePsuedoID(), phone, null);
                    mDisposable = AppAdapter.getRegistrationApi().sendVerificationCode(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnDispose(this::hideProgress)
                            .subscribe(response -> {
                                hideProgress();
                                Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_PHONE_SUCCESS);
                                Fragment fragment = EnterVerificationCodeFragment.newInstance(response.getToken(), phone);
                                replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
                            }, throwable -> {
                                hideProgress();
                                Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_PHONE_FAILURE, new APIError(throwable));
                                showSmsSendErrorAlert(throwable);
                            });*/

                }, getString(R.string.phone_number_edit),
                (dialog2, which2) -> { /*TODO negative*/ }, false);
    }

}