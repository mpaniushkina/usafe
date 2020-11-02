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
        return WizardItemFragment.newInstance(item.mDescription1, item.mDescription2, item.mDescription3, layout, item.label);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    public static class WizardItem {
        public int mDescription1;
        public int mDescription2;
        public int mDescription3;
        public int label;

        public WizardItem(int mDescription1, int mDescription2, int mDescription3, int label) {
            this.mDescription1 = mDescription1;
            this.mDescription2 = mDescription2;
            this.mDescription3 = mDescription3;
            this.label = label;
        }
    }
}
