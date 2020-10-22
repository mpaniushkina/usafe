package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.covrsecurity.io.ui.fragment.authorized.ChangeCodeInfoItem;

import java.util.ArrayList;
import java.util.List;

import static com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment.CURRENT_CODE_PAGE;
import static com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment.NEW_CODE_PAGE;
import static com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment.RE_NEW_CODE_PAGE;

public class ChangeCodeInfoPagerAdapter extends FragmentStatePagerAdapter {

    private final List<ChangeCodeInfoItem> mCodeInfoItemList = new ArrayList<ChangeCodeInfoItem>() {{
        add(ChangeCodeInfoItem.newInstance(CURRENT_CODE_PAGE));
        add(ChangeCodeInfoItem.newInstance(NEW_CODE_PAGE));
        add(ChangeCodeInfoItem.newInstance(RE_NEW_CODE_PAGE));
    }};

    public ChangeCodeInfoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public ChangeCodeInfoItem getItem(int position) {
        return mCodeInfoItemList.get(position);
    }

    @Override
    public int getCount() {
        return mCodeInfoItemList.size();
    }
}