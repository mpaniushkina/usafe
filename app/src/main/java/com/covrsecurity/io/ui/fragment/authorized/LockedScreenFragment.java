package com.covrsecurity.io.ui.fragment.authorized;

import android.content.Intent;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentLockedScreenBinding;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.fragment.BaseFragment;

public class LockedScreenFragment extends BaseFragment<FragmentLockedScreenBinding> {

    public static LockedScreenFragment newIInstance() {
        return new LockedScreenFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_locked_screen;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setSetupCovrListener(v -> {
            startActivity(new Intent(getActivity(), UnauthorizedActivity.class));
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }
}
