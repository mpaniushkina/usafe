package com.covrsecurity.io.ui.fragment.unauthorized;

import android.graphics.Color;
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

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.utils.FragmentAnimationSet;

public class WizardItemFragment extends BaseFragment {
    private static final String DESCRIPTION1 = "description1";
    private static final String DESCRIPTION2 = "description2";
    private static final String DESCRIPTION3 = "description3";
    private static final String LABEL = "label";
    private static final String LAYOUT = "layout";

    public static WizardItemFragment newInstance(int description1, int description2, int description3, int layoutRes, int label) {
        WizardItemFragment fragment = new WizardItemFragment();
        Bundle args = new Bundle();
        args.putInt(DESCRIPTION1, description1);
        args.putInt(DESCRIPTION2, description2);
        args.putInt(DESCRIPTION3, description3);
        args.putInt(LAYOUT, layoutRes);
        args.putInt(LABEL, label);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        View rootView = inflater.inflate(args.getInt(LAYOUT), container, false);
        TextView description1 = (TextView) rootView.findViewById(R.id.texDescription1);
        description1.setText(getString(args.getInt(DESCRIPTION1)));
        TextView description2 = (TextView) rootView.findViewById(R.id.texDescription2);
        description2.setText(getString(args.getInt(DESCRIPTION2)));
        TextView description3 = (TextView) rootView.findViewById(R.id.texDescription3);
        description3.setText(getString(args.getInt(DESCRIPTION3)));
        int labelTextId = args.getInt(LABEL);
        if (labelTextId != 0) {
            description2.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString spannableString = new SpannableString(getString(R.string.tut4_desc3));
            ClickableSpan termsOfUseLink = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String termsOfUseURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_terms_of_use_dev) :
                            getString(R.string.cfg_about_terms_of_use);
                    Fragment f = PrivacyPolicyFragment.newInstance(termsOfUseURL, getString(R.string.about_terms_conditions), true);
                    replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.WHITE);
                    ds.setUnderlineText(true);
                }
            };
            ClickableSpan privacyPolicyLink = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String privacyPolicyURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_privacy_policy_dev)
                            : getString(R.string.cfg_about_privacy_policy);
                    Fragment f = PrivacyPolicyFragment.newInstance(privacyPolicyURL, getString(R.string.about_privacy_policy), true);
                    replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.WHITE);
                    ds.setUnderlineText(true);
                }
            };
            spannableString.setSpan(termsOfUseLink, spannableString.length() - getString(R.string.privacy_policy_string).length() - 5 - getString(R.string.terms_of_use_string).length(),
                    spannableString.length() - getString(R.string.privacy_policy_string).length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(privacyPolicyLink, spannableString.length() - getString(R.string.privacy_policy_string).length(),
                            spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            description2.setText(spannableString);
        }
        return rootView;
    }
}