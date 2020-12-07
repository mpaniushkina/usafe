package com.covrsecurity.io.ui.fragment.unauthorized;

import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.fragment.BaseViewModelFragment;
import com.covrsecurity.io.ui.fragment.interfaces.IUnregisteredFragment;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.utils.ActivityUtils;

import timber.log.Timber;

public abstract class BaseScanFaceBiometricsFragment<Binding extends ViewDataBinding, VM extends BaseViewModel>
        extends BaseViewModelFragment<Binding, VM> implements IUnregisteredFragment {

    /**
     * @return true if this fragment needs to use UnauthorizedActivity's bottom buttons
     */
    public abstract boolean usesBottomButtons();

    private static final int ULTRA_SHORT_DELAY = 50;
    private static final int QUARTER_OF_SECOND_DELAY = 250;

    @Override
    public void onResume() {
        super.onResume();
        UnauthorizedActivity unauthorizedActivity = getUnauthorizedActivity();
        if (unauthorizedActivity == null) {
            return;
        }
        if (!usesBottomButtons()) {
            unauthorizedActivity.hideButtons();
        } else {
            ActivityUtils.scheduleOnMainThread(() -> {
                unauthorizedActivity.makeButtonsInvisible();
                ActivityUtils.scheduleOnMainThread(
                        unauthorizedActivity::showButtons,
                        ULTRA_SHORT_DELAY
                );
            }, getResources().getInteger(R.integer.fragment_transition_animation_time_medium) - QUARTER_OF_SECOND_DELAY);
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        UnauthorizedActivity unauthorizedActivity = getUnauthorizedActivity();
        if (unauthorizedActivity != null) {
            unauthorizedActivity.getRightButton().setOnClickListener(v -> {
                onNextButtonClick();
            });
        }
        enableNextButton(false);
    }

    protected void enableNextButton(boolean enable) {
        UnauthorizedActivity unauthorizedActivity = getUnauthorizedActivity();
        if (unauthorizedActivity != null) {
            unauthorizedActivity.getRightButton().setEnabled(enable);
        }
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
            unauthorizedActivity.getLeftButton().callOnClick();
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
