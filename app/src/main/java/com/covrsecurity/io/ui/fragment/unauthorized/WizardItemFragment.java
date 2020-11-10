package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentWizardScreensBinding;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.utils.FragmentAnimationSet;

public class WizardItemFragment extends BaseFragment {
    private static final String DESCRIPTION1 = "description1";
    private static final String DESCRIPTION2 = "description2";
    private static final String DESCRIPTION3 = "description3";
    private static final String LABEL = "label";
    private static final String LAYOUT = "layout";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
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
        ConstraintLayout termsOfUse = (ConstraintLayout) rootView.findViewById(R.id.termsOfUse);
        TextView description2_1 = (TextView) rootView.findViewById(R.id.texDescription2_1);
        description2_1.setText(getString(R.string.tut4_desc2_1));
        TextView description2_3 = (TextView) rootView.findViewById(R.id.texDescription2_3);
        description2_3.setText(getString(R.string.tut4_desc2_3));
        TextView description3 = (TextView) rootView.findViewById(R.id.texDescription3);
        description3.setText(getString(args.getInt(DESCRIPTION3)));
        int labelTextId = args.getInt(LABEL);
        if (labelTextId != 0) {
            description1.setVisibility(View.GONE);
            termsOfUse.setVisibility(View.VISIBLE);
            description2_1.setOnClickListener(view -> {
                String termsOfUseURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_terms_of_use_dev) :
                        getString(R.string.cfg_about_terms_of_use);
                Fragment f = PrivacyPolicyFragment.newInstance(termsOfUseURL, getString(R.string.about_terms_conditions), true);
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            });
            description2_3.setOnClickListener(view -> {
                String privacyPolicyURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_privacy_policy_dev)
                        : getString(R.string.cfg_about_privacy_policy);
                Fragment f = PrivacyPolicyFragment.newInstance(privacyPolicyURL, getString(R.string.about_privacy_policy), true);
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            });
        } else {
            description1.setVisibility(View.VISIBLE);
            termsOfUse.setVisibility(View.GONE);
        }
        return rootView;
    }
}