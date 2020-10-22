package com.covrsecurity.io.ui.fragment.unauthorized;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentInfoAboutToChooseCodeBinding;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.utils.FragmentAnimationSet;

/**
 * Created by elena on 4/28/16.
 */
public class InfoEnterCodeFragment extends BaseUnauthorizedFragment<FragmentInfoAboutToChooseCodeBinding> {

    public static Fragment newInstance() {
        return new InfoEnterCodeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_info_about_to_choose_code;
    }

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.infoText.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString spannableString = new SpannableString(getString(R.string.info_enter_code_text));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_setup_read_more_info_personal_code));
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
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
        mBinding.infoText.setText(spannableString);
    }

    @Override
    protected void onNextButtonClick() {
        super.onNextButtonClick();
        final Fragment fragment = CreatePersonalCodeFragment.newInstance(CreatePersonalCodeFragment.CreateCodeIntention.REGISTER);
        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableNextButton(true);
        ((UnauthorizedActivity) getActivity()).getRightButton().setOnClickListener((v) -> {
            onNextButtonClick();
        });
    }
}