package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentTutorialBinding;
import com.covrsecurity.io.ui.adapter.TutorialPagerAdapter;
import com.covrsecurity.io.ui.adapter.WizardPagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;

public class WizardFragment extends Fragment {

    public static Fragment newInstance() {
        return new WizardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wizard_screens, container, false);
    }

    private WizardPagerAdapter.WizardItem[] items = {
            new WizardPagerAdapter.WizardItem(R.string.tut1_desc1, R.string.tut1_desc2, R.string.tut1_desc3),
            new WizardPagerAdapter.WizardItem(R.string.tut2_desc1, R.string.tut2_desc2, R.string.tut2_desc3),
            new WizardPagerAdapter.WizardItem(R.string.tut3_desc1, R.string.tut3_desc2, R.string.tut3_desc3),
            new WizardPagerAdapter.WizardItem(R.string.tut4_desc1, R.string.tut4_desc2, R.string.tut4_desc3)
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
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (pager.getCurrentItem() == 0) {
//            // mChevronLeft.setEnabled(false);
//        }
    }
}