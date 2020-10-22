package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentDoneBinding;

/**
 * Created by elena on 4/29/16.
 */
public class DoneSetupFragment extends BaseUnauthorizedFragment<FragmentDoneBinding> {

    private static final String IS_REGISTRATION_EXTRA = "IS_REGISTRATION_EXTRA";

    public static DoneSetupFragment newInstance(boolean isRegistration) {
        final DoneSetupFragment fragment = new DoneSetupFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_REGISTRATION_EXTRA, isRegistration);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean mIsRegistration = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_done;
    }

    @Override
    public boolean usesBottomButtons() {
        return false;
    }

    @Override
    public boolean isRegistration() {
        return mIsRegistration;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIsRegistration = bundle.getBoolean(IS_REGISTRATION_EXTRA);
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setDoneClickListener(v -> startAuthorizedActivity(true));
    }

    @Override
    public boolean onBackButton() {
        return true;
    }
}
