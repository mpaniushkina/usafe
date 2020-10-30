package com.covrsecurity.io.ui.fragment.authorized.vault;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.databinding.FragmentCovrVaultAboutBinding;
import com.covrsecurity.io.greendao.model.database.CovrVaultDbModel;
import com.covrsecurity.io.utils.DatabaseOperationsWrapper;

import java.util.List;

/**
 * Created by bogdan on 06.05.2017.
 */

public class CovrVaultAboutFragment extends CovrVaultBaseChildFragment<FragmentCovrVaultAboutBinding> implements DatabaseOperationsWrapper.SuccessResult<List<CovrVaultDbModel>> {

    private boolean mFirstLoad = true;

    public static CovrVaultAboutFragment newInstance() {
        return new CovrVaultAboutFragment();
    }

    @Override
    protected void initContentBinding(FragmentCovrVaultAboutBinding aboutBinding) {
        aboutBinding.wpCovrVaultAbout.loadUrl(getString(R.string.cfg_covr_vault_about));
        if (!mFirstLoad) {
            checkIfListEmpty();
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_covr_vault_about;
    }

    @Override
    protected boolean showSecondaryToolbar() {
        return true;
    }

    @Override
    protected int getSecondaryToolbarTitle() {
        return R.string.covr_vault_about;
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirstLoad = false;
    }

    @Override
    public void onSuccess(List<CovrVaultDbModel> result) {
        if (result != null && result.size() != 0) {
            mShowExitAnimation = false;
            onBackPressed();
        }
    }

    private void checkIfListEmpty() {
        IamApp.getInstance().getDatabaseOperationsWrapper().queryAllRecordsAsync(this, true);
    }
}
