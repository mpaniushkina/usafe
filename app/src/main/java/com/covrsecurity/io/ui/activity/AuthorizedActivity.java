package com.covrsecurity.io.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.app.fcm.RegistrationIntentService;
import com.covrsecurity.io.databinding.ActivityAuthorizedBinding;
import com.covrsecurity.io.event.GetMessagesEvent;
import com.covrsecurity.io.event.HistoryBadgeEvent;
import com.covrsecurity.io.event.InternetLostEvent;
import com.covrsecurity.io.event.InternetRegainedEvent;
import com.covrsecurity.io.event.NotificationHubConnectedEvent;
import com.covrsecurity.io.event.PartnershipBadgeEvent;
import com.covrsecurity.io.event.TimerTimeoutEvent;
import com.covrsecurity.io.model.deeplink.AuthorizationDeeplink;
import com.covrsecurity.io.ui.component.AnimationEndListner;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.ui.fragment.authorized.LockScreenFragment;
import com.covrsecurity.io.ui.fragment.authorized.StandingByFragment;
import com.covrsecurity.io.ui.fragment.interfaces.OnKeyboardBackPressed;
import com.covrsecurity.io.ui.interfaces.IChildFragmentListener;
import com.covrsecurity.io.ui.viewmodel.appunlockedshared.AppUnLockedSharedViewModel;
import com.covrsecurity.io.ui.viewmodel.appunlockedshared.AppUnLockedSharedViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.authorizedactivity.AuthorizedActivityViewModel;
import com.covrsecurity.io.ui.viewmodel.authorizedactivity.AuthorizedActivityViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConnectivityUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.PlayServicesUtils;
import com.covrsecurity.io.utils.SoundUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import me.leolin.shortcutbadger.ShortcutBadger;
import timber.log.Timber;

public class AuthorizedActivity extends BaseActivity<ActivityAuthorizedBinding, AuthorizedActivityViewModel> {

    public static final String KEY_IS_ENTERED_AFTER_REGISTRATION = "IS_ENTERED_AFTER_REGISTRATION";
    public static final String KEY_IS_RESTORED_STATE = "KEY_IS_RESTORED_STATE";
    public static final String KEY_THIRD_PARTY_IN_APP_ACTIVITY_OPENED = "KEY_THIRD_PARTY_IN_APP_ACTIVITY_OPENED";
    public static final String EXTRA_AUTHORIZATION_DEEPLINK = "com.covrsecurity.io.ui.activity.EXTRA_AUTHORIZATION_DEEPLINK";

    private final int[] SOUNDS_TO_LOAD = {SoundUtils.RESPONSE_SOUND, SoundUtils.TIMEOUT_SOUND};

    @Inject
    AuthorizedActivityViewModelFactory vmFactory;
    @Inject
    AppUnLockedSharedViewModelFactory sharedVmFactory;

    private AppUnLockedSharedViewModel sharedViewModel;

    private Fragment fragmentToOpen;
    private boolean isReplacing;
    private boolean mIsEnteredAfterRegistration = false;
    @Deprecated
    private boolean isThirdPartyInAppActivityOpened;
    private boolean mIsLockScreenShown = false;
    private boolean isHistoryAllSelected = true;
    private boolean mShouldFragmentBeRetained;
    private boolean mShouldAddConnectionShown = false;
    private Runnable mPendingRunnable; //to make drawer close animation a little bit smoother
    //we may need to use this code later

    public static boolean hideLockScreen;

    @Nullable
    private AlertDialog mInternetLostDialog;

    public void setThirdPartyInAppActivityOpened(boolean thirdPartyInAppActivityOpened) { // TODO remove
        isThirdPartyInAppActivityOpened = thirdPartyInAppActivityOpened;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_authorized;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @NonNull
    @Override
    protected Class<AuthorizedActivityViewModel> getViewModelClass() {
        return AuthorizedActivityViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        hideLockScreen = false;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mIsEnteredAfterRegistration = getIntent().getBooleanExtra(KEY_IS_ENTERED_AFTER_REGISTRATION, false);
        boolean isRestoredState = savedInstanceState != null && savedInstanceState.getBoolean(KEY_IS_RESTORED_STATE);
        isThirdPartyInAppActivityOpened = savedInstanceState != null && savedInstanceState.getBoolean(KEY_THIRD_PARTY_IN_APP_ACTIVITY_OPENED);
        mIsEnteredAfterRegistration &= !isRestoredState;
        if (mIsEnteredAfterRegistration) {
            sharedViewModel = ViewModelProviders.of(this, sharedVmFactory).get(AppUnLockedSharedViewModel.class);
            sharedViewModel.resetAppUnLockedValue();
            createHubConnection();
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        replaceFragment(StandingByFragment.newInstance(), null, false);
        viewModel.pushNotificationsLiveData.observe(this, new BaseObserver<>(
                null,
                response -> {
                    Timber.d("onResponseReceived() called with: response = [%s]", response.toString());
                    AppAdapter.bus().postSticky(new GetMessagesEvent());
                },
                throwable -> Timber.e(throwable, "pushNotificationsLiveData onError")
        ));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundUtils.initSoundPool(this, SOUNDS_TO_LOAD, () -> Timber.d("Sound pool is initialized"));
        checkAndOpenLockscreen();
        if (!mShouldFragmentBeRetained) {
            if (!isThirdPartyInAppActivityOpened || IamApp.getInstance().isApplicationWasMinimized()) {
                replaceWithStandingByFragment();
            } else {
                setThirdPartyInAppActivityOpened(false);
            }
        } else {
            mShouldFragmentBeRetained = false;
        }
        IamApp.getInstance().setApplicationWasMinimized(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppAdapter.resetBadgeCount();
        }
    }

    @Override
    protected void onPause() {
        clearStandingByFragment();
        super.onPause();
    }

    @Override
    protected void onStop() {
        SoundUtils.releaseSoundPool();
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof StandingByFragment) {
            replaceWithStandingByFragment();
        }
        hideProgress();
        if (mInternetLostDialog != null) {
            mInternetLostDialog.dismiss();
            mInternetLostDialog = null;
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_RESTORED_STATE, true);
        outState.putBoolean(KEY_THIRD_PARTY_IN_APP_ACTIVITY_OPENED, isThirdPartyInAppActivityOpened);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    private AlertDialog createNoInternetAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.error_no_internet_title));
        alertDialogBuilder.setMessage(getResources().getString(R.string.error_no_internet))
                .setCancelable(false).setPositiveButton(R.string.ok, (dialog, which) -> {
            new Thread(() -> {
                if (ConnectivityUtils.hasNetworkConnection()) {
                    createHubConnection();
                }
            }).start();
            dialog.dismiss();
        });
        return alertDialogBuilder.create();
    }

    private void clearStandingByFragment() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof StandingByFragment) {
            StandingByFragment standingByFragment = (StandingByFragment) currentFragment;
            FragmentManager childFragmentManager = standingByFragment.getChildFragmentManager();
            for (int i = 0; i < childFragmentManager.getBackStackEntryCount(); i++) {
                childFragmentManager.popBackStack();
            }
            childFragmentManager.executePendingTransactions();
        }
    }

    public void replaceWithStandingByFragment() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof StandingByFragment) {
            final StandingByFragment standingByFragment = (StandingByFragment) currentFragment;
            ActivityUtils.scheduleOnMainThread(() -> {
                if (standingByFragment.isAdded()) {
                    FragmentManager childFragmentManager = standingByFragment.getChildFragmentManager();
                    for (int i = 0; i < childFragmentManager.getBackStackEntryCount(); i++) {
                        childFragmentManager.popBackStack();
                    }
                }
            });
        } else {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        }
    }

    public void clearRememberedFragment() {
        fragmentToOpen = null;
    }

    private void replaceWithRememberedFragment() {
        if (!isReplacing) {
            if (fragmentToOpen != null && !getCurrentFragment().getClass().equals(fragmentToOpen.getClass())) {
                isReplacing = true;
                replaceWithStandingByFragment();
                replaceFragment(fragmentToOpen, fragmentToOpen.getArguments(), true, FragmentAnimationSet.SLIDE_UP);
                clearRememberedFragment();
            }
        }
    }

    public void openLockScreen() {
        setupLockFragment();
        ((BaseFragment) getCurrentFragment()).onLocked();
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.lockscreen_up);
        mBinding.activitySlidingUpPanel.startAnimation(bottomUp);
        mBinding.activitySlidingUpPanel.setVisibility(View.VISIBLE);
        mIsLockScreenShown = true;
    }

    public void hideLockScreen() {
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.lockscreen_fade_away);
        bottomUp.setAnimationListener(new AnimationEndListner() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.activitySlidingUpPanel.setVisibility(View.GONE);
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(LockScreenFragment.class.getName());
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        });
        mBinding.activitySlidingUpPanel.startAnimation(bottomUp);
        mIsLockScreenShown = false;
    }

    @Deprecated
    public void createHubConnection() {
        registerWithNotificationHubs();
        viewModel.subscribeToPushNotifications();
        AppAdapter.bus().postSticky(new NotificationHubConnectedEvent());
    }

    private void checkAndOpenLockscreen() {
        if (hideLockScreen) {
            hideLockScreen();
        } else {
            if (!mIsEnteredAfterRegistration && (!isThirdPartyInAppActivityOpened || IamApp.getInstance().isApplicationWasMinimized())) {
                openLockScreen();
            } else {
                mIsEnteredAfterRegistration = false;
            }
        }
    }

    protected void setupLockFragment() {
        Timber.d("SetupLockFragment()");
        Fragment fragment = new LockScreenFragment();
        String fragmentName = fragment.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_lockscreen_container, fragment, fragmentName);
        try {
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Timber.e(e, "Error replacing fragment");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onHistoryBadgeChange(HistoryBadgeEvent event) {
        if (event != null) {
            AppAdapter.bus().removeStickyEvent(event);
            Timber.d("Changing history badge count");
            AppAdapter.settings().setHistoryBadge(event.getBadgeCount());
            if (AppAdapter.getConsumerRequests().size() == 0) {
            ShortcutBadger.applyCount(this,
                    AppAdapter.getConsumerRequests().size() + event.getBadgeCount());
            }
            AppAdapter.updateIconLauncherBadge();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void internetRegained(InternetRegainedEvent event) {
        AppAdapter.bus().removeStickyEvent(event);
        if (!mIsLockScreenShown) {
            createHubConnection();
            if (mInternetLostDialog != null) {
                mInternetLostDialog.dismiss();
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void internetLost(InternetLostEvent event) {
        AppAdapter.bus().removeStickyEvent(event);
        if (mInternetLostDialog != null) {
            mInternetLostDialog.show();
        } else {
            mInternetLostDialog = createNoInternetAlertDialog();
            mInternetLostDialog.show();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPartnershipBadgeChange(PartnershipBadgeEvent event) {
        if (event != null) {
            AppAdapter.bus().removeStickyEvent(event);
            Timber.d("Changing partnership badge count");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onTimerTimeoutReceived(TimerTimeoutEvent event) {
        int count = event.getCount();
        if (count != 0) {
            ShortcutBadger.applyCount(this, count);
        } else {
            ShortcutBadger.applyCount(this, AppAdapter.getHistoryCache().size());
        }
        AppAdapter.updateIconLauncherBadge();
        if (!ActivityUtils.getLastFragmentName().equals(StandingByFragment.class.getName())
                && !ActivityUtils.getLastFragmentName().equals(LockScreenFragment.class.getName())) {
            //TODO Hidden according to ios, should be uncommented after qa will approve
//            showToast(R.string.toast_inapp_request_timed_out);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onNotificationHubConnected(NotificationHubConnectedEvent event) {
        AppAdapter.bus().removeStickyEvent(event);
        Timber.d("AuthorizedActivity: onNotificationHubConnected() ");
    }

    @Override
    public void onBackPressed() {
        // handle backstack of child fragments
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            if (currentFragment instanceof OnKeyboardBackPressed) {
                ((OnKeyboardBackPressed) currentFragment).onKeyboardBackPressed();
            }
            // more than one entry - pop backstack
            if (currentFragment.getChildFragmentManager().getBackStackEntryCount() > 1) {
                currentFragment.getChildFragmentManager().popBackStack();
            } else if (currentFragment.getChildFragmentManager().getBackStackEntryCount() == 1) {
                // one entry - hide child fragment (by sliding down its container)
                if (currentFragment instanceof IChildFragmentListener) {
                    ((IChildFragmentListener) currentFragment).closeChildFragment();
                } else {
                    // undefined behavior
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void setShouldFragmentBeRetained(boolean shouldFragmentBeRetained) {
        this.mShouldFragmentBeRetained = shouldFragmentBeRetained;
    }

    public void setShouldAddConnectionShown(boolean shouldAddConnectionShown) {
        this.mShouldAddConnectionShown = shouldAddConnectionShown;
    }

    public boolean isShouldAddConnectionShown() {
        return this.mShouldAddConnectionShown;
    }

    public boolean isHistoryAllSelected() {
        return isHistoryAllSelected;
    }

    public void setHistoryAllSelected(boolean historyAllSelected) {
        isHistoryAllSelected = historyAllSelected;
    }

    public boolean isLockScreenShown() {
        return mIsLockScreenShown;
    }

    public AuthorizationDeeplink getAuthorizationDeeplink() {
        if (getIntent().hasExtra(EXTRA_AUTHORIZATION_DEEPLINK)) {
            AuthorizationDeeplink deeplink = (AuthorizationDeeplink) getIntent().getSerializableExtra(EXTRA_AUTHORIZATION_DEEPLINK);
            getIntent().removeExtra(EXTRA_AUTHORIZATION_DEEPLINK);
            return deeplink;
        } else {
            return null;
        }
    }

    public void registerWithNotificationHubs() {
        IamApp.getInstance().isNotificationHubInitialized = true;
        if (PlayServicesUtils.checkPlayServicesAndShowErrorDialog(this)) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
}
