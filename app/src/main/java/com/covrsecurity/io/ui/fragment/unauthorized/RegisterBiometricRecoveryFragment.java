package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentRegisterBiometricRecoveryBinding;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.registerbiometricrecovery.RegisterBiometricRecoveryViewModel;
import com.covrsecurity.io.ui.viewmodel.registerbiometricrecovery.RegisterBiometricRecoveryViewModelFactory;
import com.covrsecurity.io.utils.FingerprintUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

import static com.covrsecurity.io.ui.fragment.unauthorized.ScanFaceBiometricsFragment.CAPTURED_DOCUMENT_JPEG;
import static com.covrsecurity.io.ui.fragment.unauthorized.ScanFaceBiometricsFragment.CAPTURED_IMAGE_JPEG;
import static com.covrsecurity.io.utils.FileUtils.readAllBytes;

public class RegisterBiometricRecoveryFragment extends BaseUnauthorizedViewModelFragment<FragmentRegisterBiometricRecoveryBinding, RegisterBiometricRecoveryViewModel> {

    private static final String COVR_CODE_EXTRA = "COVR_CODE_EXTRA";
    private String takenSelfie;
    private String takenDocument;

    public static RegisterBiometricRecoveryFragment getInstance(char[] covrCode, String selfie, String documentFrontSide) {
        final RegisterBiometricRecoveryFragment fragment = new RegisterBiometricRecoveryFragment();
        Bundle args = new Bundle();
        args.putString(CAPTURED_IMAGE_JPEG, selfie);
        args.putString(CAPTURED_DOCUMENT_JPEG, documentFrontSide);
        args.putCharArray(COVR_CODE_EXTRA, covrCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    RegisterBiometricRecoveryViewModelFactory vmFactory;

    @NonNull
    @Override
    protected Class<RegisterBiometricRecoveryViewModel> getViewModelClass() {
        return RegisterBiometricRecoveryViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_biometric_recovery;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.registerBiometricRecoveryLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    moveToNextFragment();
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
        if (arguments != null) {
            takenSelfie = arguments.getString(CAPTURED_IMAGE_JPEG);
            takenDocument = arguments.getString(CAPTURED_DOCUMENT_JPEG);
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setUseRecovery(true);
        enableNextButton(true);
    }

    @Override
    protected void onNextButtonClick() {
        super.onNextButtonClick();
        final Boolean useRecovery = mBinding.getUseRecovery();
        if (useRecovery != null && useRecovery) {
            registerRecoveryRequest();
        } else {
            moveToNextFragment();
        }
    }

    private File getImageFile(String image) {
        return new File(image);
    }

    public void registerRecoveryRequest() {
         try {
            viewModel.registerRecoveryRequest(readAllBytes(getImageFile(takenSelfie)), readAllBytes(getImageFile(takenDocument)));
        } catch (IOException e) {
            Timber.e(e);
            showErrToast(e);
        }
    }

    private void moveToNextFragment() {
        final Fragment fragment;
        if (FingerprintUtils.getInstance(getActivity()).canUseFingerprintScanner(getActivity())) {
            fragment = UseFingerprintAuthFragment.newInstance(
                    getArguments().getCharArray(COVR_CODE_EXTRA),
                    true  // TODO change if this screen used multiple times
            );
        } else {
            fragment = DoneSetupFragment.newInstance(true); // TODO change if this screen used multiple times
        }
        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
    }
}
