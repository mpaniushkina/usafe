package com.covrsecurity.io.ui.fragment.unauthorized;

import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.ui.fragment.interfaces.IUnregisteredFragment;
import com.covrsecurity.io.utils.IamTools;

import timber.log.Timber;

public abstract class BaseUnauthorizedFragment<T extends ViewDataBinding> extends BaseFragment<T> implements IUnregisteredFragment {

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
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), null, false);
    }

    protected void onNextButtonClick() {
        Timber.d("On next button clicked");
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

    protected UnauthorizedActivity getUnauthorizedActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof UnauthorizedActivity) {
            return (UnauthorizedActivity) activity;
        } else {
            return null;
        }
    }

    protected void startAuthorizedActivity(final boolean isAfterRegistration) {
        IamTools.startAuthorizedActivity(getActivity(), isAfterRegistration);
    }

    @Override
    public boolean isRegistration() {
        return true;
    }
}
