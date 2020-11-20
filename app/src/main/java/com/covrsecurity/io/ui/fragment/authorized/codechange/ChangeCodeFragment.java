package com.covrsecurity.io.ui.fragment.authorized.codechange;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.common.exception.WeekPinException;
import com.covrsecurity.io.data.utils.EntityMapper;
import com.covrsecurity.io.databinding.FragmentChangeCodeCurrentBinding;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.model.error.FingerprintRecognitionError;
import com.covrsecurity.io.sdk.exception.ChangePasswordLockException;
import com.covrsecurity.io.sdk.exception.CurrentPasswordNotMatchException;
import com.covrsecurity.io.sdk.exception.NoUserDataFoundException;
import com.covrsecurity.io.sdk.storage.model.LockType;
import com.covrsecurity.io.sdk.utils.ArrayUtils;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.adapter.ChangeCodeInfoPagerAdapter;
import com.covrsecurity.io.ui.component.PersonalCodeLayout;
import com.covrsecurity.io.ui.dialog.FailedLoginDialogFragment;
import com.covrsecurity.io.ui.dialog.FingerprintAuthenticationDialogFragment;
import com.covrsecurity.io.ui.dialog.FingerprintAuthenticationFailedDialogFragment;
import com.covrsecurity.io.ui.fragment.BaseChildFragment;
import com.covrsecurity.io.ui.fragment.authorized.ChangeCodeInfoItem;
import com.covrsecurity.io.ui.interfaces.IFingerprintAuthCallBack;
import com.covrsecurity.io.ui.interfaces.IKeyboardListener;
import com.covrsecurity.io.ui.viewmodel.appunlockedshared.AppUnLockedSharedViewModel;
import com.covrsecurity.io.ui.viewmodel.appunlockedshared.AppUnLockedSharedViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.changecode.ChangeCodeViewModel;
import com.covrsecurity.io.ui.viewmodel.changecode.ChangeCodeViewModelFactory;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.DialogUtils;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ChangeCodeFragment extends BaseChildFragment<FragmentChangeCodeCurrentBinding> implements
        IKeyboardListener,
        PersonalCodeLayout.LenghtCodeChecker,
        IFingerprintAuthCallBack,
        HasSupportFragmentInjector {

    public static final int CURRENT_CODE_PAGE = 0;
    public static final int NEW_CODE_PAGE = 1;
    public static final int RE_NEW_CODE_PAGE = 2;

    private static final String DIALOG_SCANNING_TAG = "DIALOG_SCANNING_TAG";
    private static final String DIALOG_FAILED_TAG = "DIALOG_FAILED_TAG";
    private static final String DIALOG_FAILED_LOGIN_TAG = "DIALOG_FAILED_LOGIN_TAG";
    private static final String CHECK_CODE_ONLY_EXTRA = "CHECK_CODE_ONLY_EXTRA";

    public static Fragment newInstance(boolean checkCodeOnly) {
        ChangeCodeFragment fragment = new ChangeCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(CHECK_CODE_ONLY_EXTRA, checkCodeOnly);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Inject
    ChangeCodeViewModelFactory vmFactory;
    @Inject
    AppUnLockedSharedViewModelFactory sharedVmFactory;
    @Inject
    DispatchingAndroidInjector<Fragment> childFragmentInjector;

    protected ChangeCodeViewModel viewModel;
    private AppUnLockedSharedViewModel sharedViewModel;

    private final char[] mOldPin = new char[6];
    private final char[] mNewPin = new char[6];

    private FingerprintAuthenticationDialogFragment mDialogScanning;
    private FingerprintAuthenticationFailedDialogFragment mDialogFailed;
    private FailedLoginDialogFragment mFailedLoginDialog;
    private boolean isDialogFailedAdded;
    private boolean checkCodeOnly;

    private Disposable mDisposable;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_code_current;
    }

    @NonNull
    protected Class<ChangeCodeViewModel> getViewModelClass() {
        return ChangeCodeViewModel.class;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return childFragmentInjector;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, vmFactory).get(getViewModelClass());
        getLifecycle().addObserver(viewModel);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            sharedViewModel = ViewModelProviders.of(activity, sharedVmFactory).get(AppUnLockedSharedViewModel.class);
            activity.getLifecycle().addObserver(sharedViewModel);
        }
        viewModel.assessPinCodeStrengthLiveData.observe(this, new BaseObserver<>(
                null,
                response -> ActivityUtils.scheduleOnMainThread(
                        () -> changeItem(RE_NEW_CODE_PAGE),
                        ConstantsUtils.THREE_HUNDRED_MILLISECONDS
                ),
                throwable -> {
                    if (throwable instanceof WeekPinException) {
                        WeekPinException weekPinException = (WeekPinException) throwable;
                        WeekPinException.WeekPinType weekPinType = weekPinException.getWeekPinType();
                        showAlertDialog(weekPinType);
                    } else {
                        Timber.e(throwable);
                        showErrToast(throwable);
                    }
                }
        ));
        viewModel.validatePinCodeLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    if (!checkCodeOnly) {
                        ActivityUtils.scheduleOnMainThread(
                                () -> changeItem(NEW_CODE_PAGE),
                                ConstantsUtils.THREE_HUNDRED_MILLISECONDS
                        );
                    } else {
                        AppAdapter.settings().setUseFingerprintAuth(false);
                        AppAdapter.settings().clearFingerprintPasswordIv();
                        if (mDialogScanning == null) {
                            mDialogScanning = FingerprintAuthenticationDialogFragment.getInstance(getString(R.string.fingerprint_dialog_description_encrypt_with_finger));
                            mDialogScanning.initCryptoObject(Cipher.ENCRYPT_MODE, null);
                            mDialogScanning.setCallback(this);
                        }
                        if (mDialogScanning != null && !mDialogScanning.isVisible()) {
                            mDialogScanning.show(getFragmentManager(), DIALOG_SCANNING_TAG);
                        }
                    }
                },
                throwable -> {
                    hideProgress();
                    Log.w("ChangeCodeFragment", "validatePinCodeLiveData " + throwable.getMessage());
                    if (throwable instanceof CurrentPasswordNotMatchException) {

                        CurrentPasswordNotMatchException passwordNotMatchException = (CurrentPasswordNotMatchException) throwable;
                        int attemptsLeft = passwordNotMatchException.getAttemptsLeft();

                        sharedViewModel.setAppUnLockedValue(attemptsLeft);

                        Log.w("ChangeCodeFragment", "validatePinCodeLiveData: attemptsLeft = " + attemptsLeft);

                        mBinding.digitalKeyboard.shake();
                        mBinding.personCodeLL.clearText();

                        ChangeCodeInfoPagerAdapter adapter = (ChangeCodeInfoPagerAdapter) mBinding.infoPager.getAdapter();
                        ChangeCodeInfoItem item = adapter.getItem(CURRENT_CODE_PAGE);
                        item.setRemainingAttempts(attemptsLeft);
                    } else if (throwable instanceof ChangePasswordLockException) {

                        ChangePasswordLockException changePasswordLockException = (ChangePasswordLockException) throwable;

                        AppUnlockTimeEntity appUnlockTimeEntity = EntityMapper.getAppUnlockTimeEntity(changePasswordLockException);
                        sharedViewModel.setAppUnLockedValue(appUnlockTimeEntity);

                        int attemptsLeft = changePasswordLockException.getAttemptsLeft();
                        long unlockTime = changePasswordLockException.getUnlockTime();
                        LockType lockType = changePasswordLockException.getLockType();

                        mBinding.digitalKeyboard.shake();
                        mBinding.personCodeLL.clearText();

                        ChangeCodeInfoPagerAdapter adapter = (ChangeCodeInfoPagerAdapter) mBinding.infoPager.getAdapter();
                        ChangeCodeInfoItem item = adapter.getItem(CURRENT_CODE_PAGE);
                        item.setRemainingAttempts(attemptsLeft);

                        if (LockType.NOT_LOCKED != lockType) {
                            if (lockType == LockType.LOCKED_NO_TIME_OUT) {
                                unlockTime = 0;
                            }
                            mFailedLoginDialog = FailedLoginDialogFragment.getInstance(attemptsLeft, unlockTime, false, true);
                            mFailedLoginDialog.show(getFragmentManager(), DIALOG_FAILED_LOGIN_TAG);
                        }
                    } else if (throwable instanceof NoUserDataFoundException) {
                        Log.w("ChangeCodeFragment", "validatePinCodeLiveData " + throwable);
                        mBinding.digitalKeyboard.shake();
                        mBinding.digitalKeyboard.setKeyboardListener(null);
                        Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_CODE_CHANGE_ATTEMPTS_EXCEEDED);

                        DialogUtils.showOkDialog(getActivity(),
                                getString(R.string.locked_screen_header_text),
                                getString(R.string.locked_screen_main_text),
                                getString(R.string.button_setup_iam),
                                (dialog, which) -> setupCovr(),
                                false
                        );
                    }
                }
        ));
        viewModel.changePinCodeLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    /* TODO !!!
                    Bundle params = new Bundle();
                    params.putString("max_attempts", String.valueOf(MAX_ATTEMPTS_REMAINING));
                    params.putString("attempts_remaining", String.valueOf(mAttemptsRemaining));
                    */
                    Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_CODE_CHANGE/*, params*/);
                    AppAdapter.settings().clearFingerprintPasswordIv();
                    if (AppAdapter.settings().getFingerprintAuthUses()) {
                        if (mDialogScanning == null) {
                            mDialogScanning = FingerprintAuthenticationDialogFragment.getInstance(getString(R.string.fingerprint_dialog_description_encrypt_with_finger));
                            mDialogScanning.initCryptoObject(Cipher.ENCRYPT_MODE, null);
                            mDialogScanning.setCallback(this);
                        }
                        if (mDialogScanning != null && !mDialogScanning.isVisible()) {
                            mDialogScanning.show(getFragmentManager(), DIALOG_SCANNING_TAG);
                        }
                    } else {
//                        closeChildFragment();
                        onBackPressed();
                    }
                },
                throwable -> {
                    hideProgress();
                    mBinding.personCodeLL.clearText();
                    mBinding.digitalKeyboard.shake();
                    Timber.w(throwable.getMessage());
                    Toast.makeText(getActivity(), "" + throwable, Toast.LENGTH_SHORT).show();
                }
        ));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        checkCodeOnly = getArguments().getBoolean(CHECK_CODE_ONLY_EXTRA);
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.digitalKeyboard.setKeyboardListener(this);
        mBinding.personCodeLL.setLenghtCodeChecker(this);
        mBinding.infoPager.setAdapter(new ChangeCodeInfoPagerAdapter(getChildFragmentManager()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.change_pin_title);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());
    }

    @SuppressLint("NewApi")
    @Override
    public void onStop() {
        super.onStop();
        if (mDialogScanning != null) {
            mDialogScanning.setCallback(null);
            mDialogScanning.dismiss();
        }
        if (mDialogFailed != null) {
            mDialogFailed.dismiss();
        }
        if (mFailedLoginDialog != null) {
            mFailedLoginDialog.dismiss();
        }
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    protected void closeChildFragment() {
        Arrays.fill(mOldPin, (char) 0);
        Arrays.fill(mNewPin, (char) 0);
        super.closeChildFragment();
    }

    @Override
    protected void onBackPressed() {
        Arrays.fill(mOldPin, (char) 0);
        Arrays.fill(mNewPin, (char) 0);
        super.onBackPressed();
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

    @SuppressLint("NewApi")
    @Override
    public void codeLengthOK() {
        Timber.d("CODE LENGTH OK");
        char[] enteredText = mBinding.personCodeLL.getEnteredText();
        try {
            if (mBinding.personCodeLL.isCodeLayoutFull()) {
                switch (mBinding.infoPager.getCurrentItem()) {
                    case CURRENT_CODE_PAGE:
                        Timber.d("old code entered");
                        Arrays.fill(mOldPin, (char) 0);
                        System.arraycopy(enteredText, 0, mOldPin, 0, enteredText.length);
                        viewModel.validatePinCode(mOldPin);
                        break;
                    case NEW_CODE_PAGE:
                        Timber.d("new code entered");
                        Arrays.fill(mNewPin, (char) 0);
                        System.arraycopy(enteredText, 0, mNewPin, 0, enteredText.length);
                        viewModel.assessPinCodeStrength(mNewPin);
                        break;
                    case RE_NEW_CODE_PAGE:
                        Timber.d("new code re-entered");
                        if (Arrays.equals(mNewPin, enteredText)) {
                            viewModel.changePinCode(getActivity(), mOldPin, mNewPin, true);
                        } else {
                            final Handler handler = new Handler();
                            final Runnable r = () -> mBinding.personCodeLL.clearText();
                            handler.postDelayed(r, 500);
                        }
                        break;
                }
            }
        } finally {
            Arrays.fill(enteredText, (char) 0);
        }
    }

    @Override
    public void codeLengthNOK() {
        Timber.d("CODE LENGTH NOT OK");
    }

    @Override
    public void onAuthenticated(Cipher cipher) {
        char[] newPin = checkCodeOnly ? mOldPin : mNewPin;
        saveFingerprintSettings(cipher, newPin);
    }

    @SuppressLint("NewApi")
    @Override
    public void onError(FingerprintRecognitionError error) {
        Timber.e(error.getMessage());
        if (error.getCause() == FingerprintRecognitionError.MAX_ATTEMPTS) {
            if (mDialogFailed == null) {
                mDialogFailed = FingerprintAuthenticationFailedDialogFragment.getInstance(getString(R.string.fingerprint_dialog_failed_title), getString(R.string.fingerprint_dialog_failed_description_encrypt_with_finger), null);
                mDialogFailed.setDismissListener(() -> {
                    isDialogFailedAdded = false;
                    AppAdapter.settings().setUseFingerprintAuth(false);
                    onBackPressed();
                });
            }
            if (!mDialogFailed.isVisible() && !mDialogFailed.isAdded() && !isDialogFailedAdded) {
                mDialogFailed.show(getChildFragmentManager(), DIALOG_FAILED_TAG);
                isDialogFailedAdded = true;
            }
        } else if (error.getCause() == FingerprintRecognitionError.CANCEL) {
            AppAdapter.settings().setUseFingerprintAuth(false);
            onBackPressed();
        }
    }

    private void changeItem(int itemToSet) {
        mBinding.personCodeLL.clearText();
        mBinding.infoPager.setCurrentItem(itemToSet);
    }

    private void saveFingerprintSettings(Cipher cipher, char[] pin) {
        mDisposable = Single.just(ArrayUtils.charsToBytes(pin))
                .flatMapCompletable(covrCode -> Completable.fromAction(() -> {
                    byte[] encryptedBytes = cipher.doFinal(covrCode);
                    IvParameterSpec ivParams = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
                    String iv = Base64.encodeToString(ivParams.getIV(), Base64.NO_WRAP);
                    String encrypted = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
                    AppAdapter.settings().saveFingerprintPasswordIv(encrypted, iv);
                }))
                .andThen(Completable.fromAction(() -> AppAdapter.settings().setUseFingerprintAuth(true)))
                .andThen(Completable.fromAction(() -> AppAdapter.settings().setKeyFingerprintAuthTooManyAttempts(false)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::closeChildFragment, Timber::e);
                .subscribe(this::onBackPressed, Timber::e);
    }

    private void showAlertDialog(WeekPinException.WeekPinType weekPinType) {
        DialogUtils.showAlertDialog(
                getActivity(),
                getString(R.string.pincode_dialog_title_weak),
                weekPinType.getErrorMessageString(),
                getString(R.string.pincode_dialog_ok_button),
                (dialog, which) -> useWeakPin(),
                getString(R.string.pincode_dialog_cancel_button),
                (dialog, which) -> chooseNewPin(),
                false,
                true
        );
    }

    private void useWeakPin() {
        changeItem(RE_NEW_CODE_PAGE);
    }

    private void chooseNewPin() {
        mBinding.personCodeLL.clearText();
    }

    private void setupCovr() {
        startActivity(new Intent(getActivity(), UnauthorizedActivity.class));
        getActivity().finish();
    }
}
