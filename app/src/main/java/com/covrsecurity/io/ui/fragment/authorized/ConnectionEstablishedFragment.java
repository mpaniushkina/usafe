package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.GlideApp;
import com.covrsecurity.io.databinding.FragmentConnectionEstablishedBinding;
import com.covrsecurity.io.ui.fragment.BaseChildFragment;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ConnectionEstablishedFragment extends BaseChildFragment<FragmentConnectionEstablishedBinding> {

    private static final String MERCHANT_LOGO_KEY = "MERCHANT_LOGO_KEY";
    private static final String MERCHANT_FULL_NAME_KEY = "MERCHANT_FULL_NAME_KEY";

    public static Fragment newInstance(String logo, String fullName) {
        Bundle bundle = new Bundle();
        bundle.putString(MERCHANT_LOGO_KEY, logo);
        bundle.putString(MERCHANT_FULL_NAME_KEY, fullName);
        ConnectionEstablishedFragment fragment = new ConnectionEstablishedFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        if (getArguments() != null) {
            String logo = getArguments().getString(MERCHANT_LOGO_KEY);
            String fullName = getArguments().getString(MERCHANT_FULL_NAME_KEY);
            GlideApp.with(AppAdapter.context())
                    .load(logo)
                    .fitCenter()
                    .error(R.drawable.about_logo)
                    .transition(withCrossFade())
                    .into(mBinding.websiteLogoImage);
            mBinding.websiteNameText.setText(fullName);
        }
        mBinding.setDoneClickListener(v -> close());
    }

    private void close() {
        closeChildFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_connection_established;
    }
}
