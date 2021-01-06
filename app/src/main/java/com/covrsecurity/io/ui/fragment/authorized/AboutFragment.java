package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentAboutBinding;
import com.covrsecurity.io.ui.fragment.FromMenuFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.PrivacyPolicyFragment;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModel;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModelFactory;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.VersionUtils;

import javax.inject.Inject;

public class AboutFragment extends FromMenuFragment<FragmentAboutBinding, StubViewModel> {

    public static Fragment newInstance() {
        return new AboutFragment();
    }

    @Inject
    StubViewModelFactory vmFactory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @NonNull
    @Override
    protected Class<StubViewModel> getViewModelClass() {
        return StubViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setVersion(getString(R.string.about_version, VersionUtils.versionName(getActivity())));
        mBinding.setPpClickListener(v -> {
            String privacyPolicyURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_privacy_policy_dev)
                    : getString(R.string.cfg_about_privacy_policy);

            Fragment f = PrivacyPolicyFragment.newInstance(privacyPolicyURL, getString(R.string.about_privacy_policy), true);
            replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        });
        mBinding.setTouClickListener(v -> {
            String termsOfUseURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_terms_of_use_dev)
                    : getString(R.string.cfg_about_terms_of_use);

            Fragment f = PrivacyPolicyFragment.newInstance(termsOfUseURL, getString(R.string.about_terms_of_use), true);
            replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    protected int getTitleId() {
        return R.string.about;
    }
}
