package com.covrsecurity.io.ui.fragment.authorized.codechange;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentChangeCodeInfoBinding;
import com.covrsecurity.io.ui.fragment.BaseChildFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ReadMorePhoneFragment;

public class ChangeCodeInfoFragment extends BaseChildFragment<FragmentChangeCodeInfoBinding> {

    public static ChangeCodeInfoFragment newInstance(boolean checkCodeOnly) {
        return new ChangeCodeInfoFragment();
    }

    private View mCancelButton;
    private View mContinueButton;

    public static Fragment newInstance() {
        return new ChangeCodeInfoFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_code_info;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.changeCodeInfoHeader.setText(Html.fromHtml(getString(R.string.settings_change_password_info_header)));
    }

    @Override
    public void onResume() {
        super.onResume();
        mCancelButton.setOnClickListener((v) -> {
            closeChildFragment();
        });
        mContinueButton.setOnClickListener((v) -> {
            Fragment fragment = ChangeCodeFragment.newInstance(false);
            showChildFragment(
                    fragment,
                    fragment.getArguments(),
                    false,
                    R.animator.right_slide_in,
                    R.animator.left_slide_in,
                    R.animator.fade_in,
                    R.animator.left_slide_in
            );
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        TextView text = (TextView) rootView.findViewById(R.id.info_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString spannableString = new SpannableString(getString(R.string.info_enter_change_code_text));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Fragment fragment = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_setup_read_more_personal_code_change));
                showChildFragment(fragment, fragment.getArguments(), true, R.animator.fade_in, R.animator.fade_out,
                        R.animator.fade_in, R.animator.fade_out);
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
        mCancelButton = rootView.findViewById(R.id.left_bottom_button);
        mContinueButton = rootView.findViewById(R.id.right_bottom_button);
        return rootView;
    }
}
