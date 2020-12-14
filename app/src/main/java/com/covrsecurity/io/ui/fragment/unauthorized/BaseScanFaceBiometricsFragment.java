package com.covrsecurity.io.ui.fragment.unauthorized;

import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.fragment.BaseViewModelFragment;
import com.covrsecurity.io.ui.fragment.interfaces.IUnregisteredFragment;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

public abstract class BaseScanFaceBiometricsFragment<Binding extends ViewDataBinding, VM extends BaseViewModel>
        extends BaseViewModelFragment<Binding, VM> implements IUnregisteredFragment {

    private static final int ULTRA_SHORT_DELAY = 50;
    private static final int QUARTER_OF_SECOND_DELAY = 250;

    @Override
    public void onResume() {
        super.onResume();
        UnauthorizedActivity unauthorizedActivity = getUnauthorizedActivity();
        if (unauthorizedActivity == null) {
            return;
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
    }

    /**
     * @return true if call is processed and we're not callin super.onBackPressed()
     */
    public boolean onBackButton() {
        UnauthorizedActivity unauthorizedActivity = getUnauthorizedActivity();
        if (unauthorizedActivity != null) {
            unauthorizedActivity.showCancelSetupDialog();
        }
        return true;
    }

    @Nullable
    private UnauthorizedActivity getUnauthorizedActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof UnauthorizedActivity) {
            return (UnauthorizedActivity) activity;
        } else {
            return null;
        }
    }
}
