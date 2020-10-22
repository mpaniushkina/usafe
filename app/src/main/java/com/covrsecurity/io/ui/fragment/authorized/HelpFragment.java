package com.covrsecurity.io.ui.fragment.authorized;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentHelpCovrBinding;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.fragment.FromMenuFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ReadMorePhoneFragment;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModel;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModelFactory;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import javax.inject.Inject;

import zendesk.support.guide.HelpCenterActivity;

public class HelpFragment extends FromMenuFragment<FragmentHelpCovrBinding, StubViewModel> {

    public static Fragment newInstance() {
        return new HelpFragment();
    }

    @Inject
    StubViewModelFactory vmFactory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_help_covr;
    }

    @NonNull
    @Override
    protected Class<StubViewModel> getViewModelClass() {
        return StubViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setSupportListener((View v) -> {
            ((AuthorizedActivity) getActivity()).setThirdPartyInAppActivityOpened(true);
            HelpCenterActivity.builder().show(getActivity());
        });
        mBinding.setAddPartnershipListener((View v) -> {
            Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_help_read_more_how_to_add_partnership), getString(R.string.help_how_to_add_partnership));
            replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        });
        mBinding.setWhatsPartnershipListener((View v) -> {
            Fragment f = ReadMorePhoneFragment.newInstance(getString(R.string.cfg_help_read_more_whats_partnership), getString(R.string.help_what_is_partnership));
            replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        });
    }

    @Override
    protected int getTitleId() {
        return R.string.help;
    }
}
