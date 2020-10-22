package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentAboutBinding;
import com.covrsecurity.io.ui.adapter.TutorialPagerAdapter;
import com.covrsecurity.io.ui.fragment.FromMenuFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ReadMorePhoneFragment;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModel;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModelFactory;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.VersionUtils;

import java.util.Calendar;

import javax.inject.Inject;

public class AboutFragment extends FromMenuFragment<FragmentAboutBinding, StubViewModel> {

    public static Fragment newInstance() {
        return new AboutFragment();
    }

    private final TutorialPagerAdapter.TutorialItem[] items = {
            new TutorialPagerAdapter.TutorialItem(0, R.string.tut1_text, R.drawable.tut1_welcome_green),
            new TutorialPagerAdapter.TutorialItem(0, R.string.tut2_text, R.drawable.tut2_safe_green),
            new TutorialPagerAdapter.TutorialItem(0, R.string.tut3_text, R.drawable.tut3_easy_green),
            new TutorialPagerAdapter.TutorialItem(0, R.string.tut4_text, R.drawable.tut4_protect_green),
            new TutorialPagerAdapter.TutorialItem(0, R.string.tut5_text, R.drawable.tut5_start_green)
    };

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
        mBinding.setVersion(getString(R.string.about_version, VersionUtils.versionCode(getActivity()),
                VersionUtils.versionName(getActivity())));
        mBinding.setCopyright(getString(R.string.about_copyright, Calendar.getInstance().get(Calendar.YEAR)));
        mBinding.setPpClickListener(v -> {
            String privacyPolicyURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_privacy_policy_dev)
                    : getString(R.string.cfg_about_privacy_policy);

            Fragment f = ReadMorePhoneFragment.newInstance(privacyPolicyURL, getString(R.string.about_privacy_policy));
            replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        });
        mBinding.setTouClickListener(v -> {
            String termsOfUseURL = (BuildConfig.DEBUG) ? getString(R.string.cfg_about_terms_of_use_dev)
                    : getString(R.string.cfg_about_terms_of_use);

            Fragment f = ReadMorePhoneFragment.newInstance(termsOfUseURL, getString(R.string.about_terms_of_use));
            replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mBinding.pager.setAdapter(new TutorialPagerAdapter(getChildFragmentManager(), items, R.layout.item_help_pager));
        mBinding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBinding.chevronLeft.setVisibility(View.VISIBLE);
                if (position == 0) {
                    mBinding.chevronLeft.setEnabled(false);
                    mBinding.chevronRight.setEnabled(true);
                } else if (position == (items.length - 1)) {
                    mBinding.chevronLeft.setEnabled(true);
                    mBinding.chevronRight.setEnabled(false);
                } else {
                    mBinding.chevronLeft.setEnabled(true);
                    mBinding.chevronRight.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mBinding.helpIndicator.setViewPager(mBinding.pager);
        mBinding.chevronLeft.setOnClickListener((View chevronLeft) -> {
            int current = mBinding.pager.getCurrentItem();
            if (current > 0) {
                mBinding.pager.setCurrentItem(current - 1);
            }
        });
        mBinding.chevronRight.setOnClickListener((View chevronRight) -> {
            int current = mBinding.pager.getCurrentItem();
            if (current < items.length - 1) {
                mBinding.pager.setCurrentItem(current + 1);
            }
        });
        mBinding.chevronLeft.setVisibility(View.INVISIBLE);
        return v;
    }

    @Override
    protected int getTitleId() {
        return R.string.about;
    }
}
