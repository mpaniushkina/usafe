package com.covrsecurity.io.ui.fragment.unauthorized;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.common.exception.WeekPinException;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.sdk.exception.RecoverIdentityWasNotCompleted;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.adapter.InfoPagerAdapter;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.createpersonalcode.CreatePersonalCodeViewModel;
import com.covrsecurity.io.ui.viewmodel.createpersonalcode.CreatePersonalCodeViewModelFabric;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FingerprintUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.KeyboardUtils;

import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.covrsecurity.io.utils.KeyboardUtils.showKeyboard;

/**
 * Created by elena on 4/28/16.
 */
public class CreatePersonalCodeFragment extends EnterPersonalCodeFragment {

    private static final String CREATE_CODE_INTENTION_EXTRA = "CREATE_CODE_INTENTION_EXTRA";

    public static Fragment newInstance(CreateCodeIntention intention) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CREATE_CODE_INTENTION_EXTRA, intention);
        final CreatePersonalCodeFragment fragment = new CreatePersonalCodeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Inject
    CreatePersonalCodeViewModelFabric vmFactory;

    @Nullable
    AlertDialog mWeekPinTypeDialog;
    private char[] mEnteredText;
    private CreateCodeIntention mCreateCodeIntention;

    @NonNull
    @Override
    protected Class<CreatePersonalCodeViewModel> getViewModelClass() {
        return CreatePersonalCodeViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    public boolean isRegistration() {
        return mCreateCodeIntention == CreateCodeIntention.REGISTER;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.assessPinCodeStrengthLiveData.observe(this, new BaseObserver<>(
                null,
                response -> ActivityUtils.scheduleOnMainThread(
                        this::goToVerifyPage,
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
        final boolean registration = isRegistration();
        viewModel.setUpPasswordLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    if (FingerprintUtils.getInstance(getActivity()).canUseFingerprintScanner(getActivity())) {
                        Fragment fragment = UseFingerprintAuthFragment.newInstance(mEnteredText, registration);
                        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
                    } else {
                        replaceFragment(DoneSetupFragment.newInstance(registration), null, true, FragmentAnimationSet.SLIDE_LEFT);
                    }
                },
                throwable -> {
                    hideProgress();
                    Timber.e(throwable);
                    if (throwable instanceof RecoverIdentityWasNotCompleted) {
                        final UnauthorizedActivity activity = (UnauthorizedActivity) getActivity();
                        if (activity != null) {
                            DialogUtils.showOkDialog(
                                    activity,
                                    getString(R.string.recovery_was_interrupted),
                                    getString(R.string.recovery_start_from_the_beginning),
                                    getString(R.string.ok),
                                    (dialogInterface, i) -> (activity).clearData(),
                                    false
                            );
                        }
                    } else {
                        showErrToast(throwable);
                    }
                }
        ));
        viewModel.registerLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_CODE_CREATE);
                    if (FingerprintUtils.getInstance(getActivity()).canUseFingerprintScanner(getActivity())) {
                        Fragment fragment = UseFingerprintAuthFragment.newInstance(mEnteredText, registration);
                        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
                    } else {
                        replaceFragment(DoneSetupFragment.newInstance(registration), null, true, FragmentAnimationSet.SLIDE_LEFT);
                    }
                },
                throwable -> {
                    hideProgress();
                    Timber.e(throwable);
                    showErrToast(throwable);
                }
        ));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        final Bundle arguments = getArguments();
        mCreateCodeIntention = (CreateCodeIntention) arguments.getSerializable(CREATE_CODE_INTENTION_EXTRA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mBinding.infoPager.setAdapter(new InfoPagerAdapter(getChildFragmentManager()));
//        mBinding.etShowKeyboard.setVisibility(View.GONE);
//        EditText etShowKeyboard = view.findViewById(R.id.etShowKeyboard);
//        KeyboardUtils.showKeyboard(getActivity(), etShowKeyboard);
//        KeyboardUtils.showKeyboard(getActivity(), mBinding.etShowKeyboard);
        showSoftwareKeyboard(true);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mWeekPinTypeDialog != null) {
            mWeekPinTypeDialog.dismiss();
        }
    }

    @Override
    public void codeLengthOK() {
        if (mBinding.infoPager.getCurrentItem() == InfoPagerAdapter.ENTER_PAGE) {
            mEnteredText = Arrays.copyOf(mBinding.personCodeLL.getEnteredText(), mBinding.personCodeLL.getEnteredText().length);
            viewModel.assessPinCodeStrength(mEnteredText);
        } else if (mBinding.infoPager.getCurrentItem() == InfoPagerAdapter.VERIFY_PAGE) {
            if (Arrays.equals(mBinding.personCodeLL.getEnteredText(), mEnteredText)) {
                enableNextButton(true);
            } else {
                mBinding.personCodeLL.clearText();
//                mBinding.digitalKeyboard.shake();
            }
        }
    }

    @Override
    public void codeLengthNOK() {
        enableNextButton(false);
    }

    @Override
    protected boolean infoMessageIsDisplayed() {
        return false;
    }

    @Override
    protected boolean infoPagerIsDisplayed() {
        return true;
    }

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    public void showReadMoreFragment(String url) {
        Fragment f = ReadMorePhoneFragment.newInstance(url);
        replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
    }

    @Override
    protected void onNextButtonClick() {
        final boolean isRegister = mCreateCodeIntention == CreateCodeIntention.REGISTER;
        if (mBinding.infoPager.getCurrentItem() == InfoPagerAdapter.ENTER_PAGE) {
            goToVerifyPage();
        } else if (isRegister && Arrays.equals(mBinding.personCodeLL.getEnteredText(), mEnteredText)) {
//            viewModel.register(mBinding.personCodeLL.getEnteredText(), true);
            ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance(mEnteredText);
            replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        } else if (!isRegister) {
            // todo Analytics.logEvent
            viewModel.setUpPassword(mEnteredText, true);
        }
    }

    private void goToVerifyPage() {
        changeItem(InfoPagerAdapter.VERIFY_PAGE);
    }

    public void goToEnterPage() {
        Arrays.fill(mEnteredText, Character.MIN_VALUE);
        changeItem(InfoPagerAdapter.ENTER_PAGE);
    }

    private void changeItem(int itemToSet) {
        mBinding.personCodeLL.clearText();
        mBinding.infoPager.setCurrentItem(itemToSet);
    }

    private void showAlertDialog(WeekPinException.WeekPinType weekPinType) {
        mWeekPinTypeDialog = DialogUtils.showAlertDialog(
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

    private void chooseNewPin() {
        mBinding.personCodeLL.clearText();
    }

    private void useWeakPin() {
        goToVerifyPage();
    }

    protected void showSoftwareKeyboard(boolean showKeyboard) {
//        final Activity activity = getActivity();
////        if (getActivity() instanceof UnauthorizedActivity) {
////            final InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
////            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
////        }
//        View focusedView = getView().getRootView();
//        final InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        if (focusedView != null) {
////            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
////                    InputMethodManager.HIDE_NOT_ALWAYS);
//            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
//
//        }
        InputMethodManager inputMethodManager =  (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        mBinding.etShowKeyboard.requestFocus();
//        inputMethodManager.toggleSoftInputFromWindow(mBinding.etShowKeyboard.getApplicationWindowToken(),showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS, 0);
        inputMethodManager.hideSoftInputFromWindow(mBinding.etShowKeyboard.getApplicationWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public enum CreateCodeIntention {
        REGISTER,
        RECOVER
    }
}
