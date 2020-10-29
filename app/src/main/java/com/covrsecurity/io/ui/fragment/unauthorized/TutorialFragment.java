package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentTutorialBinding;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.KeyboardUtils;

import org.jetbrains.annotations.NotNull;

public class TutorialFragment extends BaseUnauthorizedFragment<FragmentTutorialBinding> {

    public static Fragment newInstance() {
        return new TutorialFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tutorial;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        KeyboardUtils.hideKeyboard(getActivity());
        mBinding.buttonCreateId.setOnClickListener((View buttonSetup) -> {
            checkForRootWithCallback((boolean isRooted) -> {
                if (!isRooted) {
                    ((UnauthorizedActivity) getActivity()).setAllowShowingBottomButtons(false);
                    Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_SETUP_START);
//                    replaceFragment(GreetingFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_UP);
                    replaceFragment(WizardFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_UP);
                }
            });
        });
        mBinding.buttonRecoverId.setOnClickListener(view1 -> {
            checkForRootWithCallback((boolean isRooted) -> {
                if (!isRooted) {
                    ((UnauthorizedActivity) getActivity()).setAllowShowingBottomButtons(true);
                    Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_RECOVERY_START);
                    replaceFragment(RecoveryQrCodeFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_UP);
                }
            });
        });
    }

    @Override
    public boolean usesBottomButtons() {
        return false;
    }

    @Override
    public boolean onBackButton() {
        return false;
    }
}
