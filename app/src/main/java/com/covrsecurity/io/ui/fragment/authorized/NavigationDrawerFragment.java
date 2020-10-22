package com.covrsecurity.io.ui.fragment.authorized;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentDrawerBinding;
import com.covrsecurity.io.ui.fragment.BaseFragment;

public class NavigationDrawerFragment extends BaseFragment<FragmentDrawerBinding> {

    public static Fragment newInstance() {
        return new NavigationDrawerFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_drawer;
    }
}
