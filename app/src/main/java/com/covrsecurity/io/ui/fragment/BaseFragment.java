package com.covrsecurity.io.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.event.NoInternetEvent;
import com.covrsecurity.io.ui.activity.BaseActivity;
import com.covrsecurity.io.ui.fragment.interfaces.OnKeyboardBackPressed;
import com.covrsecurity.io.ui.interfaces.IFragmentListener;
import com.covrsecurity.io.ui.interfaces.IRootResultListener;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.RootChecker;

public abstract class BaseFragment<Binding extends ViewDataBinding> extends Fragment implements OnKeyboardBackPressed {

    protected Binding mBinding;

    protected IFragmentListener fragmentListener;

    private static AlertDialog mRootDialog;

    protected abstract int getLayoutId();

    @Deprecated
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData(savedInstanceState);
        initTools();
        initBinding(inflater);
        return mBinding.getRoot();
    }

    protected void initBinding(LayoutInflater inflater) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), null, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        cleanRequests();
    }

    @Override
    public void onResume() {
        if (saveAsLastFragment()) {
            ActivityUtils.setLastFragmentName(this.getClass().getName());
        }
        getAnalyticsScreenName();
        //Analytics.logEvent(getActivity(), getAnalyticsScreenName(), getAnalyticsParams());
        super.onResume();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachActivity((BaseActivity) activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachActivity((BaseActivity) context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentListener.dismissErrDlg();
    }

    @CallSuper
    protected void initData(Bundle savedInstanceState) {
    }

    @CallSuper
    protected void initTools() {
    }

    private Bundle getAnalyticsParams() {
        return null;
    }

    private String getAnalyticsScreenName() {
        return this.getClass().getName();
    }

    /**
     * This is used to exclude StandingBy (requests lis) fragment from showing Toast upon new request
     *
     * @return
     */
    protected boolean saveAsLastFragment() {
        return true;
    }

    protected void onAttachActivity(BaseActivity activity) {
        try {
            fragmentListener = activity;
        } catch (ClassCastException classCastException) {
            throw new IllegalAccessError("Activity MUST implement IFragmentListener");
        }
    }

    protected void cleanRequests() {

    }

    protected void hideProgress() {
        fragmentListener.hideProgress();
    }

    protected void showProgress() {
        fragmentListener.showProgress();
    }


    protected void showToast(int msgId) {
        String msg = AppAdapter.resources().getString(msgId);
        showToast(msg);
    }

    protected void showToast(String msg) {
        ActivityUtils.scheduleOnMainThread(() ->
                Toast.makeText(AppAdapter.context(), msg, Toast.LENGTH_SHORT).show());
    }

    public void onLocked() {
    }

    protected void replaceFragment(Fragment fragment, Bundle bundle, boolean addToBackStack) {
        fragmentListener.replaceFragment(fragment, bundle, addToBackStack);
    }

    protected void replaceFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, int containerViewId,
                                   int showAnimationId, int hideAnimationId) {
        fragmentListener.replaceFragment(fragment, bundle, addToBackStack, containerViewId, showAnimationId, hideAnimationId);
    }

    protected void replaceFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                   FragmentAnimationSet transitionAnimations) {
        fragmentListener.replaceFragment(fragment, bundle, addToBackStack, transitionAnimations);
    }

    protected void showNoInternet() {
        fragmentListener.onNoInternetReceived(new NoInternetEvent());
    }

    protected void showErrDialog(Throwable t, DialogInterface.OnClickListener listener) {
        fragmentListener.showErrDlg(t, listener);
    }

    protected void showNoInternetDlg(DialogInterface.OnClickListener listener) {
        fragmentListener.showNoInternetDlg(listener);
    }

    protected void showErrToast(Throwable t) {
        fragmentListener.showErrToast(t);
    }

    @Override
    public void onKeyboardBackPressed() {
    }

    protected void onBackPressed() {
        FragmentActivity fa = getActivity();
        if (fa != null) {
            fa.onBackPressed();
        }
    }

    protected void finishActivity() {
        FragmentActivity fa = getActivity();
        if (fa != null) {
            fa.finish();
        }
    }

    // for false alarm use com.stericson.RootShell.RootShell
    protected boolean isPhoneRooted() {
        return RootChecker.isDeviceRooted();
    }

    protected void checkForRootWithCallback(final IRootResultListener rootResultListener) {

        if (isPhoneRooted()) {
            if (mRootDialog != null && mRootDialog.isShowing() && getActivity() != null && !getActivity().isFinishing()) {
                mRootDialog.dismiss();
            }
            mRootDialog = DialogUtils.showOkDialog(getActivity(), getString(R.string.error_rooted_device), false, (dialog, which) -> {
                mRootDialog = null;
                if (rootResultListener != null) {
                    rootResultListener.onRootResult(true);
                }
            });
        } else {
            if (rootResultListener != null) {
                rootResultListener.onRootResult(false);
            }
        }
    }

    protected void showNoInternetDialog() {
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            ((BaseActivity) activity).showNoInternetDialog();
        }
    }

    protected void showServerErrorDialog() {
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            ((BaseActivity) activity).showServerErrorDialog();
        }
    }
}
