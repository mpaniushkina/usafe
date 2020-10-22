package com.covrsecurity.io.ui.fragment.authorized;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentAddPartnershipBinding;
import com.covrsecurity.io.ui.fragment.BaseChildFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ReadMorePhoneFragment;
import com.covrsecurity.io.ui.interfaces.IKeyboardListener;
import com.covrsecurity.io.utils.AnimationUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;

/**
 * Created by elena on 5/11/16.
 */
public class AddPartnershipFragment extends BaseChildFragment<FragmentAddPartnershipBinding> implements IKeyboardListener {

    private static final int MAX_CODE_LENGTH = 4;
    private int mDigitAnimationLength;
    private TextView[] mDigitViews;
    private StringBuilder mCode;

    //    private AddPartnershipRequest mAddPartnershipRequest;
    private AlertDialog dialogParingSuccess;
    private AlertDialog dialogParingFailed;

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        initRequests();
        initDigitViews();
        mBinding.setCancelClickListener(v -> close());
        mBinding.setContinueClickListener(v -> {
            showProgress();
//            mAddPartnershipRequest.send(getmCode().toString());
        });
        enableNextButton(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        TextView text = (TextView) v.findViewById(R.id.tv_phone_subheader);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString spannableString = new SpannableString(getString(R.string.add_partnership_info));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_setup_read_more_add_partnership));
                addChildFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString
                .setSpan(clickableSpan, spannableString.length() - getString(R.string.read_more).length(),
                        spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(spannableString);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialogParingSuccess != null && dialogParingSuccess.isShowing()) {
            dialogParingSuccess.dismiss();
        }
        if (dialogParingFailed != null && dialogParingFailed.isShowing()) {
            dialogParingFailed.dismiss();
        }
    }


    private void close() {
        closeChildFragment();
    }

    protected void initRequests() {
//        mAddPartnershipRequest = new AddPartnershipRequest(new IResponseListener<Partnership>() {
//            @Override
//            public void onResponseReceived(Partnership response) {
//                hideProgress();
//                AppAdapter.setPartnerships(new ArrayList<Partnership>() {{
//                    add(response);
//                }});
//                Analytics.logEvent(getActivity(), Analytics.EVENT_ADD_PARTNERSHIP_SUCCESS);
//                dialogParingSuccess = DialogUtils.showOkDialog(
//                        getActivity(),
//                        getString(R.string.add_partnership_pairing_successful_title),
//                        String.format(getString(R.string.add_partnership_pairing_successful), response.getCompany().getName()),
//                        null,
//                        (dialog, which) -> close(), true);
//                Timber.d("Partnership added: %s", response.toString());
//            }
//
//            @Override
//            public void onError(APIError error) {
//                hideProgress();
//                Analytics.logEvent(getActivity(), Analytics.EVENT_ADD_PARTNERSHIP_FAILURE);
//                dialogParingFailed = DialogUtils.showOkDialog(
//                        getActivity(),
//                        getString(R.string.add_partnership_pairing_failed_title),
//                        getString(R.string.add_partnership_pairing_failed),
//                        null,
//                        (dialog, which) -> close(), true);
//                Timber.d("Error adding partnership: %s", error.toString());
//            }
//        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_partnership;
    }

    public static Fragment newInstance() {
        return new AddPartnershipFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDigitAnimationLength = getResources().getInteger(R.integer.digit_fade_time);
        mCode = new StringBuilder();
    }

    protected void initDigitViews() {
        mDigitViews = new TextView[]{
                mBinding.phoneNumberEtContainer.digit1,
                mBinding.phoneNumberEtContainer.digit2,
                mBinding.phoneNumberEtContainer.digit3,
                mBinding.phoneNumberEtContainer.digit4};
        mBinding.digitalKeyboard.setKeyboardListener(this);
        mBinding.deleteButton.setOnClickListener((View deleteButton) -> {
            removeDigit();
        });
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
        if (mCode.length() < MAX_CODE_LENGTH) {
            mCode.append(value);
            AnimationUtils.showDigitAnimation(mDigitViews[mCode.length() - 1], value, mDigitAnimationLength);
        }
        if (mCode.length() == MAX_CODE_LENGTH) {
            enableNextButton(true);
        }
    }

    private void removeDigit() {
        if (mCode.length() > 0) {
            mCode.deleteCharAt(mCode.length() - 1);
            AnimationUtils.hideDigitAnimation(mDigitViews[mCode.length()], mDigitAnimationLength);
        }
        if (mCode.length() < MAX_CODE_LENGTH) {
            enableNextButton(false);
        }
    }

    private void enableNextButton(boolean state) {
        mBinding.rightBottomButton.setEnabled(state);
    }

    public StringBuilder getmCode() {
        return mCode;
    }

}

