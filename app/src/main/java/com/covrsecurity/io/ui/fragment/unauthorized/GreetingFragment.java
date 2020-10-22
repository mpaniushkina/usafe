package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentGreetingBinding;
import com.covrsecurity.io.ui.component.LinkTouchMovementMethod;
import com.covrsecurity.io.ui.component.TouchableSpan;
import com.covrsecurity.io.utils.FragmentAnimationSet;

public class GreetingFragment extends BaseUnauthorizedFragment<FragmentGreetingBinding> {

    public static GreetingFragment newInstance() {
        return new GreetingFragment();
    }

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_greeting;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        enableNextButton(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvGreeting = (TextView) view.findViewById(R.id.info_text);
        String greetingString = tvGreeting.getText().toString();
        tvGreeting.setMovementMethod(LinkTouchMovementMethod.getInstance());

        SpannableString spannableString = new SpannableString(tvGreeting.getText().toString());
        setTextSizesToSpannable(spannableString, greetingString);
        setColorsToSpannable(spannableString, greetingString);
        ClickableSpan csTermsOfUse = new TouchableSpan(getColor(R.color.greeting_text_green), getColor(R.color.greeting_text_green), getColor(R.color.faded_tealish)) {
            @Override
            public void onClick(View widget) {
                String termsOfUseURL = (BuildConfig.DEBUG) ?
                        getString(R.string.cfg_about_terms_of_use_dev) :
                        getString(R.string.cfg_about_terms_of_use);

                Fragment f = ReadMorePhoneFragment.newInstance(termsOfUseURL);
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            }
        };
        ClickableSpan csPrivacyPolicy = new TouchableSpan(getColor(R.color.greeting_text_green), getColor(R.color.greeting_text_green), getColor(R.color.faded_tealish)) {
            @Override
            public void onClick(View widget) {
                String privacy_policy_URL = (BuildConfig.DEBUG) ?
                        getString(R.string.cfg_about_privacy_policy_dev)
                        : getString(R.string.cfg_about_privacy_policy);
                Fragment f = ReadMorePhoneFragment.newInstance(privacy_policy_URL);
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            }
        };
        spannableString.setSpan(csTermsOfUse, greetingString.indexOf(getString(R.string.terms_of_use)),
                greetingString.indexOf(getString(R.string.terms_of_use)) + getString(R.string.terms_of_use).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(csPrivacyPolicy, greetingString.indexOf(getString(R.string.privascy_policy)),
                greetingString.indexOf(getString(R.string.privascy_policy)) + getString(R.string.privascy_policy).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvGreeting.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    private void setTextSizesToSpannable(SpannableString string, String sourse) {
        string.setSpan(new AbsoluteSizeSpan(getDimensionValue(R.dimen.greetint_text_top)),
                0, sourse.indexOf(getString(R.string.terms_of_use)) - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new AbsoluteSizeSpan(getDimensionValue(R.dimen.greetint_text_middle)),
                sourse.indexOf(getString(R.string.terms_of_use)), sourse.indexOf(getString(R.string.substring_by_continuing)) - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new AbsoluteSizeSpan(getDimensionValue(R.dimen.greetint_text_bottom)),
                sourse.indexOf(getString(R.string.substring_by_continuing)), sourse.length() - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private int getDimensionValue(int dimID) {
        return (int) AppAdapter.resources().getDimensionPixelSize(dimID);
    }

    private void setColorsToSpannable(SpannableString string, String sourse) {
        string.setSpan(new ForegroundColorSpan(getColor(R.color.greeting_text_gray)),
                0, sourse.indexOf(getString(R.string.terms_of_use)) - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new ForegroundColorSpan(getColor(R.color.greeting_text_green)),
                sourse.indexOf(getString(R.string.terms_of_use)), sourse.indexOf(getString(R.string.substring_by_continuing)) - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new ForegroundColorSpan(getColor(R.color.greeting_text_light_gray)),
                sourse.indexOf(getString(R.string.substring_by_continuing)), sourse.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private int getColor(int colorID) {
        return AppAdapter.resources().getColor(colorID);
    }

    @Override
    protected void onNextButtonClick() {
        super.onNextButtonClick();
        Fragment fragment = InfoEnterCodeFragment.newInstance();
        replaceFragment(fragment, null, true,
                FragmentAnimationSet.SLIDE_LEFT);
    }

    public boolean onBackButton() {
        //((UnauthorizedActivity) getActivity()).getLeftButton().callOnClick();
        return false;
    }
}
