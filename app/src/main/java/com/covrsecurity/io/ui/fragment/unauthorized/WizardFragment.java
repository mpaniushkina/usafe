package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentWizardScreensBinding;
import com.covrsecurity.io.ui.adapter.WizardPagerAdapter;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;

public class WizardFragment extends BaseUnauthorizedFragment<FragmentWizardScreensBinding> {

    public static Fragment newInstance() {
        return new WizardFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wizard_screens;
    }

    private WizardPagerAdapter.WizardItem[] items = {
            new WizardPagerAdapter.WizardItem(R.string.tut1_desc1, R.string.tut1_desc2, R.string.description_empty, R.string.description_empty, R.string.tut1_desc3),
            new WizardPagerAdapter.WizardItem(R.string.tut2_desc1, R.string.tut2_desc2, R.string.description_empty, R.string.description_empty, R.string.tut2_desc3),
            new WizardPagerAdapter.WizardItem(R.string.tut3_desc1, R.string.tut3_desc2, R.string.description_empty, R.string.description_empty, R.string.tut3_desc3),
            new WizardPagerAdapter.WizardItem(R.string.tut4_desc1, R.string.tut4_desc2_1, R.string.tut4_desc2_2, R.string.tut4_desc2_3, R.string.tut4_desc3)
    };

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager pager = view.findViewById(R.id.pager);
        Button next = view.findViewById(R.id.buttonNext);
        CirclePageIndicator indicator = view.findViewById(R.id.indicator);
        pager.setAdapter(new WizardPagerAdapter(getChildFragmentManager(), items, R.layout.item_wizard_pager));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == items.length - 1) {
                    next.setText(R.string.button_iam_ready);
                } else {
                    next.setText(R.string.tutorial_next_arrow);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        indicator.setViewPager(pager);
        next.setOnClickListener((View chevronRight) -> {
            int current = pager.getCurrentItem();
            if (current < items.length - 1) {
                pager.setCurrentItem(current + 1);
            }
            if (current == items.length - 1) {
                Fragment f = CreatePersonalCodeFragment.newInstance(CreatePersonalCodeFragment.CreateCodeIntention.REGISTER);
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            }
        });
    }

    @Override
    public boolean usesBottomButtons() {
        return false;
    }
}