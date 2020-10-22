package com.covrsecurity.io.ui.fragment.authorized;

import android.annotation.SuppressLint;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentFingerprintErrorMessageBinding;
import com.covrsecurity.io.ui.fragment.BaseChildFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ReadMorePhoneFragment;
import com.covrsecurity.io.utils.FingerprintUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.StringUtils;

/**
 * Created by Lenovo on 28.12.2016.
 */

public class FingerprintErrorMessageFragment extends BaseChildFragment<FragmentFingerprintErrorMessageBinding> {

    public static FingerprintErrorMessageFragment newInstance() {
        return new FingerprintErrorMessageFragment();
    }

    @Override
    @SuppressLint({"NewApi"})
    @SuppressWarnings({"MissingPermission"})
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.useFingerprintGroup.setVisibility(View.GONE);
        mBinding.setUseFingerprint(AppAdapter.settings().getFingerprintAuthUses());
        FingerprintUtils instance = FingerprintUtils.getInstance(getContext());
        if (!instance.readyToUseFingerprintScanner(getContext())) {
            mBinding.useFingerprintGroup.setVisibility(View.GONE);
            mBinding.title.setText(R.string.fingerprint_auth_available_title);
            mBinding.description.setMovementMethod(LinkMovementMethod.getInstance());
            mBinding.description.setText(StringUtils.getClickableSpannable(
                    getContext().getString(R.string.fingerprint_auth_available_description, getContext().getString(R.string.read_more)),
                    getContext().getString(R.string.read_more), () -> {
                        Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_about_fingerprint));
                        addChildFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
                    }));
        } else {
            mBinding.useFingerprintGroup.setVisibility(View.VISIBLE);
            mBinding.title.setText(R.string.fingerprint_auth_ready_title);
            mBinding.description.setText(R.string.fingerprint_auth_ready_description);
        }
        mBinding.setCloseClickListener(v -> closeChildFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fingerprint_error_message;
    }

}
