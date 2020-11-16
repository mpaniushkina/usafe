package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.covrsecurity.io.ui.fragment.authorized.ConnectionsItemFragment;

public class ConnectionsPagerAdapter extends FragmentStatePagerAdapter {

    private ConnectionsPage[] pages;
    private int layout;

    public ConnectionsPagerAdapter(FragmentManager fm, ConnectionsPage[] pages, int layout) {
        super(fm);
        this.pages = pages;
        this.layout = layout;


    }

    @Override
    public Fragment getItem(int position) {
        ConnectionsPage item = pages[position];
        return ConnectionsItemFragment.newInstance(item.mDescription, layout);
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    public static class ConnectionsPage {
        public int mDescription;

        public ConnectionsPage(int mDescription) {
            this.mDescription = mDescription;
        }
    }
}
