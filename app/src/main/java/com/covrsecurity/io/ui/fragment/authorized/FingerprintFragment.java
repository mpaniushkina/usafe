package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentFingerprintBinding;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment;
import com.covrsecurity.io.utils.FingerprintUtils;

import org.jetbrains.annotations.NotNull;

public class FingerprintFragment extends BaseFragment<FragmentFingerprintBinding> {

    private boolean mReadyToUseFingerprintScanner;
    private boolean fingerprintAuthUses;

    public static Fragment newInstance() {
        return new FingerprintFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fingerprint;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.fingerprint_title);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());
        mReadyToUseFingerprintScanner = FingerprintUtils.getInstance(getActivity()).readyToUseFingerprintScanner(getActivity());
        fingerprintAuthUses = AppAdapter.settings().getFingerprintAuthUses();
        mBinding.setFingerprintEnable(AppAdapter.settings().getFingerprintAuthUses());
//        mBinding.fingerprintUnlockState.setImageResource(fingerprintAuthUses ? R.drawable.item_approved : R.drawable.item_declined);
//        mBinding.fingerprintUnlocking.setOnClickListener(view1 -> {
//            if (mReadyToUseFingerprintScanner && !fingerprintAuthUses) {
//                Fragment fragment = ChangeCodeFragment.newInstance(true, true);
//                replaceFragment(fragment, fragment.getArguments(), true);
//                mBinding.fingerprintUnlockState.setImageResource(R.drawable.item_approved);
//            } else if (mReadyToUseFingerprintScanner) {
//                AppAdapter.settings().setUseFingerprintAuth(false);
//                fingerprintAuthUses = false;
//                mBinding.fingerprintUnlockState.setImageResource(R.drawable.item_declined);
//            } else {
//                AppAdapter.settings().setUseFingerprintAuth(false);
//                mBinding.fingerprintUnlockState.setImageResource(R.drawable.item_declined);
//                replaceFragment(FingerprintErrorMessageFragment.newInstance(), null, true);
//            }
//        });
        mBinding.fingerprintUnlocking.setOnClickListener(view1 -> {
            if (mReadyToUseFingerprintScanner && !fingerprintAuthUses) {
                mBinding.setFingerprintEnable(true);
                Fragment fragment = ChangeCodeFragment.newInstance(true, true);
                replaceFragment(fragment, fragment.getArguments(), true);
            } else if (mReadyToUseFingerprintScanner) {
                AppAdapter.settings().setUseFingerprintAuth(false);
                fingerprintAuthUses = false;
                mBinding.setFingerprintEnable(false);
            } else {
                mBinding.setFingerprintEnable(false);
                AppAdapter.settings().setUseFingerprintAuth(false);
                replaceFragment(FingerprintErrorMessageFragment.newInstance(), null, true);
            }
        });
    }
}
