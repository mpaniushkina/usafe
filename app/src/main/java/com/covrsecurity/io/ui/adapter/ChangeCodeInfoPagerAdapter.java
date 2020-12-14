package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.covrsecurity.io.ui.fragment.authorized.ChangeCodeInfoItem;
import com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment;

import java.util.ArrayList;
import java.util.List;

import static com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment.isFingerprintSetup;

public class ChangeCodeInfoPagerAdapter extends FragmentStatePagerAdapter {

    private final List<ChangeCodeInfoItem> mCodeInfoItemList = new ArrayList<ChangeCodeInfoItem>() {{
        add(ChangeCodeInfoItem.newInstance(ChangeCodeFragment.CURRENT_CODE_PAGE, isFingerprintSetup));
        add(ChangeCodeInfoItem.newInstance(ChangeCodeFragment.NEW_CODE_PAGE, false));
        add(ChangeCodeInfoItem.newInstance(ChangeCodeFragment.RE_NEW_CODE_PAGE, false));
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