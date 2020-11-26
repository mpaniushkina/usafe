package com.covrsecurity.io.ui.fragment.unauthorized;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.common.exception.WeekPinException;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.model.Fields;
import com.covrsecurity.io.sdk.exception.RecoverIdentityWasNotCompleted;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.adapter.InfoPagerAdapter;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.createpersonalcode.CreatePersonalCodeViewModel;
import com.covrsecurity.io.ui.viewmodel.createpersonalcode.CreatePersonalCodeViewModelFabric;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModel;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModelFactory;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.DialogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import timber.log.Timber;

import static com.covrsecurity.io.sdk.utils.QrCodeRequestResource.qrCodeStringValue;

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
    @Inject
    PartnershipViewModelFactory addConnectionVmFactory;
    private PartnershipViewModel addConnectionViewModel;

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
        addConnectionViewModel = ViewModelProviders.of(getActivity(), addConnectionVmFactory).get(PartnershipViewModel.class);
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
                    startAuthorizedActivity(true);
//                    ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance(mEnteredText);
//                    replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
//                    if (FingerprintUtils.getInstance(getActivity()).canUseFingerprintScanner(getActivity())) {
//                        Fragment fragment = UseFingerprintAuthFragment.newInstance(mEnteredText, registration);
//                        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
//                    } else {
//                        replaceFragment(DoneSetupFragment.newInstance(registration), null, true, FragmentAnimationSet.SLIDE_LEFT);
//                    }
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
                    addConnectionViewModel.getQrCodeConnection(Fields.GET_QRCODE_COMPANY_USER_ID, Fields.GET_QRCODE_TRANSACTION_ID);
//                    if (FingerprintUtils.getInstance(getActivity()).canUseFingerprintScanner(getActivity())) {
//                        Fragment fragment = UseFingerprintAuthFragment.newInstance(mEnteredText, registration);
//                        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
//                    } else {
//                        replaceFragment(DoneSetupFragment.newInstance(registration), null, true, FragmentAnimationSet.SLIDE_LEFT);
//                    }
                },
                throwable -> {
                    hideProgress();
                    Timber.e(throwable);
                    showErrToast(throwable);
                }
        ));
        addConnectionViewModel.qrCodeConnectionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();
                    String mQrCode;
                    mQrCode = qrCodeStringValue;
                    String decodedQrCode = qrCodeStringValue;
                    try {
                        decodedQrCode = URLDecoder.decode(mQrCode, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("utf8", "conversion", e);
                    }
                    addConnectionViewModel.addConnection(decodedQrCode);
                },
                throwable -> {
                    hideProgress();
                    Timber.e("" + throwable);
                    Timber.e(throwable);
                    showErrToast(throwable);
                }
        ));
        addConnectionViewModel.addConnectionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    startAuthorizedActivity(true);
                },
                throwable -> {
                    hideProgress();
                    Timber.e("" + throwable);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mBinding.infoPager.setAdapter(new InfoPagerAdapter(getChildFragmentManager()));
        mBinding.closeButton.setOnClickListener(view1 -> onBackButton());
        mBinding.backButton.setOnClickListener(view1 -> {
                    if (mBinding.infoPager.getCurrentItem() == InfoPagerAdapter.VERIFY_PAGE) {
                        goToEnterPage();
                    } else {
                        ((UnauthorizedActivity) Objects.requireNonNull(getActivity())).goBack();
                    }
                });
        mBinding.pinCodeTitle.setText(mBinding.infoPager.getCurrentItem() == InfoPagerAdapter.ENTER_PAGE ?
                R.string.enter_code_header : R.string.verify_code_header);
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
                final boolean isRegister = mCreateCodeIntention == CreateCodeIntention.REGISTER;
                if (mBinding.infoPager.getCurrentItem() == InfoPagerAdapter.ENTER_PAGE) {
                    goToVerifyPage();
                } else if (isRegister && Arrays.equals(mBinding.personCodeLL.getEnteredText(), mEnteredText)) {
                     viewModel.register(mBinding.personCodeLL.getEnteredText(), true);
//                    ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance(mEnteredText);
//                    replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
                } else if (!isRegister) {
                    viewModel.setUpPassword(mEnteredText, true);
                }
            } else {
                final Handler handler = new Handler();
                final Runnable r = () -> mBinding.personCodeLL.clearText();
                handler.postDelayed(r, 500);
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

    @Override
    protected void onNextButtonClick() {
        final boolean isRegister = mCreateCodeIntention == CreateCodeIntention.REGISTER;
        if (mBinding.infoPager.getCurrentItem() == InfoPagerAdapter.ENTER_PAGE) {
            goToVerifyPage();
        } else if (isRegister && Arrays.equals(mBinding.personCodeLL.getEnteredText(), mEnteredText)) {
            viewModel.register(mBinding.personCodeLL.getEnteredText(), true);
//            ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance(mEnteredText);
//            replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
        } else if (!isRegister) {
            // todo Analytics.logEvent
            viewModel.setUpPassword(mEnteredText, true);
        }
    }

    private void goToVerifyPage() {
        changeItem(InfoPagerAdapter.VERIFY_PAGE);
        mBinding.pinCodeTitle.setText(R.string.verify_code_header);
    }

    public void goToEnterPage() {
        Arrays.fill(mEnteredText, Character.MIN_VALUE);
        changeItem(InfoPagerAdapter.ENTER_PAGE);
        mBinding.pinCodeTitle.setText(R.string.enter_code_header);
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

    @Override
    public void onBackspaceButtonClick() {
        mBinding.personCodeLL.eraseNumber();
    }

    public enum CreateCodeIntention {
        REGISTER,
        RECOVER
    }
}
