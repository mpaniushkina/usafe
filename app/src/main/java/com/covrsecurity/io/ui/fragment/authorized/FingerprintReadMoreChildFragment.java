package com.covrsecurity.io.ui.fragment.authorized;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentPhoneReadMoreBinding;
import com.covrsecurity.io.ui.fragment.BaseChildFragment;

/**
 * Created by Lenovo on 03.02.2017.
 */

public class FingerprintReadMoreChildFragment extends BaseChildFragment<FragmentPhoneReadMoreBinding> {

    private static final String INFO_URL = "INFO_URL";

    public static Fragment newInstance(String url) {
        Fragment fragment = new FingerprintReadMoreChildFragment();
        Bundle b = new Bundle();
        b.putString(INFO_URL, url);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setDoneClickListener(v -> closeChildFragment());
        Bundle args = getArguments();
        mBinding.wvInfo.loadUrl(args.getString(INFO_URL));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone_read_more;
    }

}