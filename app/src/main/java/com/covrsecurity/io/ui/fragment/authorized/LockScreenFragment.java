package com.covrsecurity.io.ui.fragment.authorized;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.common.ZipUtils;
import com.covrsecurity.io.data.utils.EntityMapper;
import com.covrsecurity.io.databinding.FragmentLockscreenBinding;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.event.NotificationHubConnectedEvent;
import com.covrsecurity.io.model.Push;
import com.covrsecurity.io.model.TimerInfo;
import com.covrsecurity.io.model.error.FingerprintRecognitionError;
import com.covrsecurity.io.sdk.exception.AppLockedException;
import com.covrsecurity.io.sdk.exception.NoUserDataFoundException;
import com.covrsecurity.io.sdk.utils.ArrayUtils;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.component.PersonalCodeLayout;
import com.covrsecurity.io.ui.component.TimersLayout;
import com.covrsecurity.io.ui.dialog.FailedLoginDialogFragment;
import com.covrsecurity.io.ui.dialog.FingerprintAuthenticationDialogFragment;
import com.covrsecurity.io.ui.dialog.FingerprintAuthenticationFailedDialogFragment;
import com.covrsecurity.io.ui.fragment.BaseViewModelFragment;
import com.covrsecurity.io.ui.interfaces.IFingerprintAuthCallBack;
import com.covrsecurity.io.ui.interfaces.IKeyboardListener;
import com.covrsecurity.io.ui.viewmodel.appunlockedshared.AppUnLockedSharedViewModel;
import com.covrsecurity.io.ui.viewmodel.appunlockedshared.AppUnLockedSharedViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.lockscreent.LockScreenViewModel;
import com.covrsecurity.io.ui.viewmodel.lockscreent.LockScreenViewModelFactory;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConnectivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.IamTools;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FingerprintUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.LogUtil;
import com.covrsecurity.io.utils.TrueTimeUtils;
import com.instacart.library.truetime.TrueTime;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.ConnectException;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LockScreenFragment extends BaseViewModelFragment<FragmentLockscreenBinding, LockScreenViewModel> implements
        IKeyboardListener,
        PersonalCodeLayout.LenghtCodeChecker {

    private static final int FIVE_SECONDS = 5000;
    private static final int FIVETEEN_SECONDS = 15000;

    @Inject
    LockScreenViewModelFactory vmFactory;
    @Inject
    AppUnLockedSharedViewModelFactory sharedVmFactory;

    private AppUnLockedSharedViewModel sharedViewModel;

    private int size = 0;
    private String mInfoMessageOneTimer;
    private String mInfoMessageSeveralTimers;
    private volatile boolean mInitialTimersStarted = false;

    private FingerprintAuthenticationDialogFragment mDialogScanning;
    private FingerprintAuthenticationFailedDialogFragment mDialogFailed;
    private FailedLoginDialogFragment mFailedLoginDialog;

    private boolean isDialogFailedAdded;
    private boolean isDialogScanningCancelled;

    private Timer timer;
    private TimerTask timerTaskRefresh;
    private final Handler handler = new Handler();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_lockscreen;
    }

    @NonNull
    @Override
    protected Class<LockScreenViewModel> getViewModelClass() {
        return LockScreenViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            sharedViewModel = ViewModelProviders.of(activity, sharedVmFactory).get(AppUnLockedSharedViewModel.class);
            activity.getLifecycle().addObserver(sharedViewModel);
        }
        viewModel.getAppUnlockTimeLiveData.observe(this, new BaseObserver<>(
                null,
                response -> {
                    sharedViewModel.setAppUnLockedValue(response);
                    consumeAppUnlockTime(response);
                },
                Timber::e
        ));
        viewModel.unLockLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                unlocked -> {
                    hideProgress();
                    if (unlocked) {
                        hideLockScreen();
                        AppAdapter.bus().postSticky(new NotificationHubConnectedEvent());
                    } else {
//                        mBinding.digitalKeyboard.shake();
                        mBinding.personCodeLL.clearText();
//                        mFailedLoginDialog = FailedLoginDialogFragment.getInstance(attemptsLeft, 0, false, true);
//                        mFailedLoginDialog.show(getFragmentManager(), DIALOG_FAILED_LOGIN_TAG);
                    }
                },
                throwable -> {
                    hideProgress();
//                    mBinding.digitalKeyboard.shake();
                    mBinding.personCodeLL.clearText();
                    if (throwable instanceof NoUserDataFoundException) {
                        AppAdapter.settings().dropAll();
                        IamApp.getInstance().clearApplicationData();
                        ((AuthorizedActivity) getActivity()).hideLockScreen();
                        replaceFragment(LockedScreenFragment.newIInstance(), null,
                                false, FragmentAnimationSet.FADE_IN_NO_EXIT);
                    } else if (throwable instanceof AppLockedException) {
                        AppLockedException appLockedException = (AppLockedException) throwable;
                        AppUnlockTimeEntity appUnlockTimeEntity = EntityMapper.getAppUnlockTimeEntity(appLockedException);
                        sharedViewModel.setAppUnLockedValue(appUnlockTimeEntity);
                        showFailedLoginDialog(appLockedException.getAttemptsLeft(), appLockedException.getUnlockTime());
                    } else {
                        Timber.e(throwable);
                        showErrToast(throwable);
                    }
                }
        ));
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        isDialogScanningCancelled = false;
        mBinding.digitalKeyboard.setKeyboardListener(this);
        mBinding.addPushButton.setOnClickListener((View v) -> {
            TimersLayout timers = mBinding.timersLayout;
            timers.addTimer(generateTimerInfo(1));
            onTimerAdded();
        });
        mBinding.timersLayout.setListener(new TimersLayout.TimersListener() {
            @Override
            public void onViewInitialized(TimersLayout v) {

                Single<ArrayList<Long>> pendingRequestsSingle = Single.fromCallable(() -> AppAdapter.settings().getPendingRequest())
                        .map(pendingRequests -> {
                            if (!TextUtils.isEmpty(pendingRequests)) {
                                return LogUtil.getGson().fromJson(pendingRequests, ConstantsUtils.PENDING_REQUESTS_TYPE);
                            } else {
                                return new ArrayList<Long>();
                            }
                        })
                        .subscribeOn(Schedulers.io());


                Single<Date> trueTimeSingle = TrueTimeUtils.initializeTrueTime()
                        .retry(5)
                        .subscribeOn(Schedulers.io());

                Single<ArrayList<Long>> combineSingle = ZipUtils.zip(
                        pendingRequestsSingle,
                        trueTimeSingle,
                        (longs, date) -> longs
                );

                Disposable disposable = Single.fromCallable(() -> ConnectivityUtils.isConnected(getActivity()) && ConnectivityUtils.hasNetworkConnection())
                        .flatMap(isConnected -> isConnected ? Single.just(true) : Single.error(new ConnectException()))
                        .flatMap(isConnected -> combineSingle)
                        .flatMapObservable(Observable::just)
                        .flatMapIterable(requests -> requests)
                        .map(expireTime -> expireTime * ConstantsUtils.MILLISECONDS_IN_SECOND)
                        .filter(expireTime -> expireTime > TrueTime.now().getTime())
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                longs -> setInitialTimers(longs, false),
                                throwable -> {
                                    Timber.w(throwable);
                                    if (throwable instanceof ConnectException) {
                                        DialogUtils.showOkDialog(getActivity(), AppAdapter.resources().getString(R.string.error_no_internet), false);
                                    } else {
                                        showErrToast(throwable);
                                    }
                                });
                mCompositeDisposable.add(disposable);
            }

            @Override
            public void onInitialTimersStarted() {
                mInitialTimersStarted = true;
            }

            @Override
            public void onTimerFinished() {
                onTimerRemoved();
            }
        });
    }

    @SuppressLint("NewApi")
    private void initFingerprintDialog() {
        Pair<String, String> passwordIv = AppAdapter.settings().getFingerprintPasswordIv();
        if (AppAdapter.settings().getFingerprintAuthUses()
                && !AppAdapter.settings().getFingerprintAuthTooManyAttempts()
                && FingerprintUtils.getInstance(getActivity()).readyToUseFingerprintScanner(getActivity())
                && !TextUtils.isEmpty(passwordIv.second)) {
            if (mDialogScanning == null) {
                mDialogScanning = FingerprintAuthenticationDialogFragment.getInstance();
                mDialogScanning.initCryptoObject(Cipher.DECRYPT_MODE, Base64.decode(passwordIv.second, Base64.NO_WRAP));
                mDialogScanning.setCallback(new IFingerprintAuthCallBack() {
                    @Override
                    public void onAuthenticated(Cipher cipher) {
                        Disposable disposable = Single.fromCallable(() -> getCovrCode(cipher))
                                .map(ArrayUtils::bytesToChars)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(password -> viewModel.unLock(password),
                                        throwable -> {
//                                            mBinding.digitalKeyboard.shake();
                                            if (mDialogFailed == null) {
                                                mDialogFailed = FingerprintAuthenticationFailedDialogFragment.getInstance(getString(R.string.fingerprint_something_wrong_happened_title), getString(R.string.fingerprint_dialog_failed_could_not_decrypt_description), null);
                                                mDialogFailed.setDismissListener(() -> isDialogFailedAdded = false);
                                            }
                                            if (!mDialogFailed.isVisible() && !mDialogFailed.isAdded() && !isDialogFailedAdded) {
                                                mDialogFailed.show(getChildFragmentManager());
                                                isDialogFailedAdded = true;
                                            }
                                            if (throwable.getCause() != null && KeyStoreException.class.getSimpleName().equals(throwable.getCause().getClass().getSimpleName())) {
                                                AppAdapter.settings().setUseFingerprintAuth(false);
                                            }
                                        });
                        mCompositeDisposable.add(disposable);
                    }

                    @Override
                    public void onError(FingerprintRecognitionError error) {
                        if (error.getCause() == FingerprintRecognitionError.MAX_ATTEMPTS) {
//                            mBinding.digitalKeyboard.shake();
                            if (mDialogFailed == null) {
                                mDialogFailed = FingerprintAuthenticationFailedDialogFragment.getInstance();
                                mDialogFailed.setDismissListener(() -> isDialogFailedAdded = false);
                            }
                            if (!mDialogFailed.isVisible() && !mDialogFailed.isAdded() && !isDialogFailedAdded) {
                                mDialogFailed.show(getChildFragmentManager());
                                isDialogFailedAdded = true;
                            }
                        } else if (error.getCause() == FingerprintRecognitionError.CANCEL) {
                            isDialogScanningCancelled = true;
                        }
                    }
                });
            }
        }
    }

    private void showFailedLoginDialog(int attemptsLeft, long unlockedTime) {
        if (mFailedLoginDialog != null) {
            mFailedLoginDialog.dismiss();
        }
        mFailedLoginDialog = FailedLoginDialogFragment.getInstance(attemptsLeft, unlockedTime, true, false);
        mFailedLoginDialog.show(getChildFragmentManager());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private byte[] getCovrCode(Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        Pair<String, String> passwordIv = AppAdapter.settings().getFingerprintPasswordIv();
        if (passwordIv.first == null || passwordIv.second == null) {
            throw new IllegalStateException("No Covr code saved");
        }
        byte[] covrCodeBytes = Base64.decode(passwordIv.first, Base64.NO_WRAP);
        return cipher.doFinal(covrCodeBytes);
    }

    private void onTimerRemoved() {
        Timber.d("On timer removed");
        if (size > 0) {
            size--;
            setInfoMessageTimersCount(size);
        }
    }

    private void onTimerAdded() {
        Timber.d("On timer added");
        size++;
        setInfoMessageTimersCount(size);
    }

    private void setInfoMessageTimersCount(int count) {
        Timber.d("Activity != null: %b", getActivity() != null);
        if (getActivity() != null) {
            if (count == 0) {
                mBinding.infoMessage.setVisibility(View.INVISIBLE);
            } else if (count == 1) {
                mBinding.infoMessage.setVisibility(View.VISIBLE);
                if (mInfoMessageOneTimer != null) {
                    mBinding.infoMessage.setText(mInfoMessageOneTimer);
                }
            } else {
                mBinding.infoMessage.setVisibility(View.VISIBLE);
                if (mInfoMessageSeveralTimers != null) {
                    mBinding.infoMessage.setText(String.format(mInfoMessageSeveralTimers, count));
                }
            }
        }
    }

    private void setInitialTimers(List<Long> expireDates, boolean update) {
        LinkedList<TimerInfo> timersInfo = getTimersInfo(expireDates);
        size = timersInfo.size();
        Timber.d("!! set initial timers: requestCount -  %d", size);
        ActivityUtils.scheduleOnMainThread(() -> {
            if (!update) {
                mBinding.timersLayout.setInitialTimers(timersInfo);
            } else {
                mBinding.timersLayout.updateTimers(timersInfo);
            }
            setInfoMessageTimersCount(size);
        });
    }

    private LinkedList<TimerInfo> getTimersInfo(List<Long> expireDates) {
        LinkedList<TimerInfo> timerInfos = new LinkedList<>();
        for (Long expireDate : expireDates) {
            TimerInfo info = new TimerInfo(expireDate - 5 * ConstantsUtils.MINUTES_IN_SECOND * ConstantsUtils.MILLISECONDS_IN_SECOND, expireDate);
            timerInfos.add(info);
        }
        return timerInfos;
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        checkForRootWithCallback((boolean isRooted) -> {
            if (isRooted) {
                Timber.e("Device is rooted. Logging out user");
                startActivity(new Intent(getActivity(), UnauthorizedActivity.class)
                        .putExtra(ConstantsUtils.INTENT_KEY_FOR_TUTORIAL_FRAGMENT, true));
                getActivity().finish();
            }
        });
        AppAdapter.bus().register(this);
        mInitialTimersStarted = false;
        mInfoMessageOneTimer = getString(R.string.lock_screen_you_have_one_pending_request);
        mInfoMessageSeveralTimers = getString(R.string.lock_screen_you_have_x_pending_requests);
        Log.d("LockScreen", "onResume");
        startTimer();
        if (!isDialogScanningCancelled) {
            initFingerprintDialog();
            AppUnlockTimeEntity appUnlockTimeEntity = sharedViewModel.getAppUnlockTime();
            if (appUnlockTimeEntity != null) {
                consumeAppUnlockTime(appUnlockTimeEntity);
            } else {
                viewModel.getAppUnlockTime();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void consumeAppUnlockTime(AppUnlockTimeEntity response) {
        if (response.getUnlockedTime() - System.currentTimeMillis() > ConstantsUtils.MILLISECONDS_IN_SECOND) {
            showFailedLoginDialog(response.getAttemptsLeft(), response.getUnlockedTime());
        } else if (mDialogScanning != null && !mDialogScanning.isVisible()) {
            if (mDialogScanning.isEnrolledFingersDidNotChange()) {
                mDialogScanning.show(getChildFragmentManager());
            } else {
                if (mDialogFailed == null) {
                    mDialogFailed = FingerprintAuthenticationFailedDialogFragment.getInstance(getString(R.string.fingerprint_something_wrong_happened_title), getString(R.string.fingerprint_dialog_failed_could_not_decrypt_description), null);
                    mDialogFailed.setDismissListener(() -> isDialogFailedAdded = false);
                }
                if (!mDialogFailed.isVisible() && !mDialogFailed.isAdded() && !isDialogFailedAdded) {
                    mDialogFailed.show(getChildFragmentManager());
                    isDialogFailedAdded = true;
                }
                AppAdapter.settings().setUseFingerprintAuth(false);
            }
        }
    }

    private void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTaskRefresh, 10, 3000);
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTaskRefresh = new TimerTask() {
            public void run() {
//                handler.post(() -> mBinding.blurringView.invalidate());
            }
        };
    }

    @Override
    @SuppressLint("NewApi")
    public void onPause() {
        mBinding.timersLayout.stop();
        if (mDialogScanning != null) {
            mDialogScanning.setCallback(null);
            mDialogScanning.dismiss();
            mDialogScanning = null;
        }
        if (mDialogFailed != null) {
            mDialogFailed.dismiss();
//            mDialogFailed = null;
        }
        if (mFailedLoginDialog != null) {
            mFailedLoginDialog.dismiss();
            mFailedLoginDialog = null;
        }
        stopTimerTask();
        Log.d("LockScreen", "onPause");
        AppAdapter.bus().unregister(this);
        super.onPause();
    }

    private void hideLockScreen() {
        mBinding.digitalKeyboard.setKeyboardListener(null);
        mBinding.personCodeLL.clearText();
        AppAdapter.settings().setKeyFingerprintAuthTooManyAttempts(false);
        ((AuthorizedActivity) getActivity()).hideLockScreen();
        ((AuthorizedActivity) getActivity()).createHubConnection();
    }

    private TimerInfo generateTimerInfo(int index) {
        Random r = new SecureRandom();
        long curTime = TrueTime.now().getTime();
        long startTime = curTime - FIVE_SECONDS;
        long endTime = curTime + index * FIVE_SECONDS + r.nextInt(FIVE_SECONDS) + FIVETEEN_SECONDS;
        //Timber.d("Timer info created %d - %d", startTime, endTime);
        return new TimerInfo(startTime, endTime);
    }

    protected boolean infoMessageIsDisplayed() {
        return true;
    }

    @Override
    public void codeLengthOK() {
        viewModel.unLock(mBinding.personCodeLL.getEnteredText());
    }

    private void forceReRegistration() {
        hideProgress();
        DialogUtils.showOkDialog(getActivity(),
                getString(R.string.migration_not_possible_title),
                getString(R.string.migration_not_possible_message),
                getString(R.string.ok),
                (dialog, which) -> IamTools.reRegistration(getActivity()), false);
    }

    @Override
    public void codeLengthNOK() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (infoMessageIsDisplayed()) {
            mBinding.infoMessage.setVisibility(View.VISIBLE);
        }
        mBinding.personCodeLL.setLenghtCodeChecker(this);
//        mBinding.blurringView.setBlurredView(getActivity().findViewById(R.id.empty_view));
        return view;
    }

    @Override
    public void onKeyboardButtonClick(boolean clickNotInterceded, char value) {
        if (clickNotInterceded) {
            mBinding.personCodeLL.numberEntered(value);
        } else {
            showToast(R.string.touch_been_intercepted_alert);
        }
    }

    @Override
    public void onBackspaceButtonClick() {
        mBinding.personCodeLL.eraseNumber();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCompositeDisposable.dispose();
    }

    @Override
    protected boolean saveAsLastFragment() {
        return false;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPushReceived(Push event) {
        AppAdapter.bus().removeStickyEvent(event);
        Disposable pendingRequestDisposable = Observable.just(AppAdapter.settings().getPendingRequest())
                .map(pendingRequests -> {
                    if (!TextUtils.isEmpty(pendingRequests)) {
                        return LogUtil.getGson().fromJson(pendingRequests, ConstantsUtils.PENDING_REQUESTS_TYPE);
                    } else {
                        return new ArrayList<Long>();
                    }
                })
                .flatMapIterable(objects -> objects)
                .map(expireTime -> expireTime * ConstantsUtils.MILLISECONDS_IN_SECOND)
                .filter(expireTime -> TrueTime.now().getTime() < expireTime)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(longs -> setInitialTimers(longs, true));
        mCompositeDisposable.add(pendingRequestDisposable);
    }
}
