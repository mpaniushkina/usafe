package com.covrsecurity.io.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.CovrApp;
import com.covrsecurity.io.domain.exception.BlockCurrentAppException;
import com.covrsecurity.io.domain.exception.ForceUpdateException;
import com.covrsecurity.io.event.NoInternetEvent;
import com.covrsecurity.io.event.SessionConflictEvent;
import com.covrsecurity.io.ui.dialog.ProgressDialogFragment;
import com.covrsecurity.io.ui.fragment.authorized.StandingByFragment;
import com.covrsecurity.io.ui.interfaces.IFragmentListener;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.baseactivity.BaseActivityViewModel;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.CovrTools;
import com.covrsecurity.io.utils.DeviceIntegrityHelper;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.ErrorMsgHelper;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.NotificationUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import timber.log.Timber;

public abstract class BaseActivity<Binding extends ViewDataBinding, VM extends BaseActivityViewModel>
        extends DaggerAppCompatActivity
        implements IFragmentListener {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    protected Binding mBinding;

    private boolean mIsActivityRestored;
    private ProgressDialogFragment mProgressDialog;

    private ErrorMsgHelper errMsgHelper = null;

    protected abstract int getLayoutId();

    protected abstract int getFragmentContainerId();

    protected VM viewModel;

    @NonNull
    protected abstract Class<VM> getViewModelClass();

    @NonNull
    protected abstract ViewModelProvider.Factory getViewModelFactory();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );
        super.onCreate(savedInstanceState);
        errMsgHelper = new ErrorMsgHelper();
        viewModel = ViewModelProviders.of(this, getViewModelFactory()).get(getViewModelClass());
        getLifecycle().addObserver(viewModel);
        NotificationUtils.clearNotification(this);
        initData(savedInstanceState);
        initBinding();
        initViews();
        viewModel.pushNotificationsLiveData.observe(this, new BaseObserver<>(
                null,
                response -> setAppVersionCheckDone(),
                throwable -> {
                    Timber.e(throwable);
                    if (throwable instanceof ForceUpdateException) {
                        setAppVersionCheckDone();
                        forceUpdate();
                    } else if (throwable instanceof BlockCurrentAppException) {
                        setAppVersionCheckDone();
                        blockCurrentApp();
                    } else {
                        Timber.e(throwable);
                    }
                }
        ));
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @CallSuper
    protected void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsActivityRestored = true;
        }
    }

    protected boolean isActivityRestored() {
        return mIsActivityRestored;
    }

    @Override
    protected void onStart() {
        super.onStart();
        CovrApp covrApp = CovrApp.getInstance();

        if (covrApp.isForceUpgrade()) {
            forceUpdate();
        } else if (covrApp.isAppBlocked()) {
            blockCurrentApp();
        } else if (covrApp.isApplicationWasMinimized() || !covrApp.isInitialVersionCheckDone()) {
            checkAppVersion();
            if (this instanceof AuthorizedActivity) {
                DeviceIntegrityHelper.checkAndWarnForMaliciousApps(this);
            }
        }
        AppAdapter.bus().register(this);
    }

    @Override
    protected void onStop() {
        try {
            AppAdapter.bus().unregister(this);
            dismissErrDlg();
        } catch (Throwable t) {
            Timber.e(t);
        }
        super.onStop();
    }

    @CallSuper
    protected void initViews() {
        setUpStatusBarColor();
    }

    private void setUpStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.covr_green));
        }
    }

    @Override
    public void replaceFragment(Fragment fragment, Bundle bundle,
                                boolean addToBackStack) {
        replaceFragment(fragment, bundle, addToBackStack, getFragmentContainerId(), 0, 0);
    }

    @Override
    public void replaceFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                FragmentAnimationSet animations) {
        replaceFragment(fragment, bundle, addToBackStack, getFragmentContainerId(),
                animations.enter, animations.exit, animations.popEnter, animations.popExit);
    }

    public void replaceFragment(Fragment fragment, Bundle bundle,
                                boolean addToBackStack, int containerViewId,
                                int showAnimationId, int hideAnimationId) {
        replaceFragment(fragment, bundle, addToBackStack, getFragmentContainerId(), showAnimationId, 0, 0, hideAnimationId);
    }

    public void replaceFragment(Fragment fragment, Bundle bundle,
                                boolean addToBackStack, int containerViewId,
                                int enterAnimationId, int exitAnimationId,
                                int popEnterAnimationId, int popExitAnimationId) {
        String fragmentName = fragment.getClass().getName();
        Timber.d("Replacing fragment with %s", fragmentName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(fragmentName);
        }
        Fragment f = Fragment.instantiate(BaseActivity.this, fragmentName, bundle);
        f.setTargetFragment(fragment.getTargetFragment(), fragment.getTargetRequestCode());
        if (enterAnimationId != 0 && popExitAnimationId != 0) {
            ft.setCustomAnimations(enterAnimationId, exitAnimationId, popEnterAnimationId, popExitAnimationId);
        }
        ft.replace(containerViewId, f, fragmentName);
        try {
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Timber.e(e, "Error replacing fragment");
        }
    }

    protected final Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(getFragmentContainerId());
    }


    public void clearBackStack() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showProgress() {
        hideProgress();
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialogFragment();
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(ProgressDialogFragment.class.getSimpleName());
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment != null) {
            ft.remove(fragment);
        }
        mProgressDialog = new ProgressDialogFragment();
        ft.add(mProgressDialog, ProgressDialogFragment.class.getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
        }
    }

    public void showToast(int msgId) {
        String msg = AppAdapter.resources().getString(msgId);
        showToast(msg);
    }

    public void showToast(String msg) {
        ActivityUtils.scheduleOnMainThread(() ->
                Toast.makeText(AppAdapter.context(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onNoInternetReceived(NoInternetEvent event) {
        showNoInternetDialog();
    }

    public void showNoInternetDialog() {
        hideProgress();
        errMsgHelper.showNoInternetDialog(this);
    }

    public void showServerErrorDialog() {
        hideProgress();
        errMsgHelper.showServerErrorDialog(this);
    }

    public void showUnknownErrorDialog(String message) {
        hideProgress();
        errMsgHelper.showUnknownErrorDialog(this, message);
    }

    private void showSessionConflictErrorDialog() {
        hideProgress();
        errMsgHelper.showSessionConflictErrorDialog(this,
                (dialog, which) -> CovrTools.openUnautorizedActivity(BaseActivity.this));
    }

    public void showErrDlg(Throwable t, DialogInterface.OnClickListener listener) {
        errMsgHelper.showErrDlg(this, t, listener);
    }

    public void showNoInternetDlg(DialogInterface.OnClickListener listener) {
        errMsgHelper.showNoInternetDlg(this, listener);
    }

    @Override
    public void dismissErrDlg() {
        errMsgHelper.dismiss();
    }

    public void showErrToast(Throwable t) {
        errMsgHelper.showErrToast(t);
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onSessionConflictReceived(SessionConflictEvent event) {  // todo #1
//        AppAdapter.settings().dropAuthCookie();
        CovrApp.getInstance().clearApplicationData();
        AppAdapter.settings().dropAll();
        stopStandingByUpdates();
        showSessionConflictErrorDialog();
    }

    public void stopStandingByUpdates() {
        if (getCurrentFragment() instanceof StandingByFragment) {
            ((StandingByFragment) getCurrentFragment()).stopUpdates();
        }
    }

    private void checkAppVersion() {
        viewModel.isApproved();
    }

    private void blockCurrentApp() {
        CovrApp.getInstance().setAppIsBlocked();
        DialogUtils.showOkDialog(this,
                getString(R.string.version_mismatched_dialog_message),
                false, (dialog, which) -> finish());
    }

    private void forceUpdate() {
        CovrApp.getInstance().setForceUpgrade();
        DialogUtils.showOkDialog(this,
                getString(R.string.force_update_dialog_update_message),
                false, (dialog, which) -> {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" +
                                "https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    finish();
                });
    }

    private void setAppVersionCheckDone() {
        CovrApp.getInstance().setInitialVersionCheckDone();
    }
}
