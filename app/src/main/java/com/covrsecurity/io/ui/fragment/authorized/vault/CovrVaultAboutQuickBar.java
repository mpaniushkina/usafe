package com.covrsecurity.io.ui.fragment.authorized.vault;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentCovrVaultAboutQuickBarBinding;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModel;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModelFactory;

import javax.inject.Inject;

/**
 * Created by Lenovo on 08.06.2017.
 */

public class CovrVaultAboutQuickBar extends CovrVaultBaseParentFragment<FragmentCovrVaultAboutQuickBarBinding> {

    public static CovrVaultAboutQuickBar newInstance() {
        return new CovrVaultAboutQuickBar();
    }

    @Inject
    StubViewModelFactory vmFactory;

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.covrVaultAboutQuickBarWebview.loadUrl(getString(R.string.cfg_covr_vault_about_quickbar));
    }

    @Override
    public void closeChildFragment() {
    }

    @Override
    public void showChildFragment() {
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_covr_vault_about_quick_bar;
    }

    @Override
    protected int getTitleId() {
        return R.string.covr_vault_about_quickbar;
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
}
