package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.covrsecurity.io.ui.fragment.unauthorized.WizardItemFragment;

public class WizardPagerAdapter extends FragmentStatePagerAdapter {

    private WizardItem[] items;
    private int layout;

    public WizardPagerAdapter(FragmentManager fm, WizardItem[] items, int layout) {
        super(fm);
        this.items = items;
        this.layout = layout;
    }

    @Override
    public Fragment getItem(int position) {
        WizardItem item = items[position];
        return WizardItemFragment.newInstance(item.mDescription1, item.mDescription2_1, item.mDescription2_2, item.mDescription2_3, item.mDescription3, layout);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    public static class WizardItem {
        public int mDescription1;
        public int mDescription2_1;
        public int mDescription2_2;
        public int mDescription2_3;
        public int mDescription3;

        public WizardItem(int mDescription1, int mDescription2_1, int mDescription2_2, int mDescription2_3, int mDescription3) {
            this.mDescription1 = mDescription1;
            this.mDescription2_1 = mDescription2_1;
            this.mDescription2_2 = mDescription2_2;
            this.mDescription2_3 = mDescription2_3;
            this.mDescription3 = mDescription3;
        }
    }
}
