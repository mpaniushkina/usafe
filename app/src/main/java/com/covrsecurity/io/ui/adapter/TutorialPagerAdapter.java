package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.covrsecurity.io.ui.fragment.unauthorized.TutorialItemFragment;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {
    private TutorialItem[] items;
    private int layout;

    public TutorialPagerAdapter(FragmentManager fm, TutorialItem[] items, int layout) {
        super(fm);
        this.items = items;
        this.layout = layout;
    }

    @Override
    public Fragment getItem(int position) {
        TutorialItem item = items[position];
        return TutorialItemFragment.newInstance(item.mLabel, item.mDescription, layout, item.mDrawableId);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    public static class TutorialItem {
        public int mLabel;
        public int mDescription;
        public int mDrawableId;

        public TutorialItem(int mLabel, int mDescription, int mDrawableId) {
            this.mLabel = mLabel;
            this.mDescription = mDescription;
            this.mDrawableId = mDrawableId;
        }
    }
}