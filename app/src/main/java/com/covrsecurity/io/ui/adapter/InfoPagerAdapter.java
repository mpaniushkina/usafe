package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.covrsecurity.io.ui.fragment.unauthorized.InfoItemFragment;

public class InfoPagerAdapter extends FragmentStatePagerAdapter {

    public static final int ENTER_PAGE = 0;
    public static final int VERIFY_PAGE = 1;

    private static final int INFO_PAGES = 2;

    public InfoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return InfoItemFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return INFO_PAGES;
    }
}