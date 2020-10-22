package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentTutorialBinding;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.adapter.TutorialPagerAdapter;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import org.jetbrains.annotations.NotNull;

public class TutorialFragment extends BaseUnauthorizedFragment<FragmentTutorialBinding> {

    public static Fragment newInstance() {
        return new TutorialFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tutorial;
    }

    private TutorialPagerAdapter.TutorialItem[] items = {
            new TutorialPagerAdapter.TutorialItem(R.string.tut1_label, R.string.tut1_text, R.drawable.tut1_welcome_green),
            new TutorialPagerAdapter.TutorialItem(R.string.tut2_label, R.string.tut2_text, R.drawable.tut2_safe_green),
            new TutorialPagerAdapter.TutorialItem(R.string.tut3_label, R.string.tut3_text, R.drawable.tut3_easy_green),
            new TutorialPagerAdapter.TutorialItem(R.string.tut4_label, R.string.tut4_text, R.drawable.tut4_protect_green),
            new TutorialPagerAdapter.TutorialItem(R.string.tut5_label, R.string.tut5_text, R.drawable.tut5_start_green)
    };

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.pager.setAdapter(new TutorialPagerAdapter(getChildFragmentManager(), items, R.layout.item_tutorial_pager));
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
        mBinding.indicator.setViewPager(mBinding.pager);
        mBinding.buttonSetupCovr.setOnClickListener((View buttonSetup) -> {
            checkForRootWithCallback((boolean isRooted) -> {
                if (!isRooted) {
                    ((UnauthorizedActivity) getActivity()).setAllowShowingBottomButtons(true);
                    Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_SETUP_START);
                    replaceFragment(GreetingFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_UP);
                }
            });
        });
        mBinding.buttonRecoverCovr.setOnClickListener(view1 -> {
            checkForRootWithCallback((boolean isRooted) -> {
                if (!isRooted) {
                    ((UnauthorizedActivity) getActivity()).setAllowShowingBottomButtons(true);
                    Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_RECOVERY_START);
                    replaceFragment(RecoveryQrCodeFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_UP);
                }
            });
        });
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding.pager.getCurrentItem() == 0) {
            // mChevronLeft.setEnabled(false);
        }
    }

    @Override
    public boolean usesBottomButtons() {
        return false;
    }

    @Override
    public boolean onBackButton() {
        return false;
    }
}
