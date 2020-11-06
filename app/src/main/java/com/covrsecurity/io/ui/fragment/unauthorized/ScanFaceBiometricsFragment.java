package com.covrsecurity.io.ui.fragment.unauthorized;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentScanFaceBiometricsBinding;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.model.BiometricsVerification;
import com.covrsecurity.io.model.error.BioMetricDataProvideCancel;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.fragment.TestFragment;
import com.covrsecurity.io.ui.fragment.authorized.SettingsFragment;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.biometricsshared.BiometricsSharedViewModel;
import com.covrsecurity.io.ui.viewmodel.biometricsshared.BiometricsSharedViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.scanfacebiometrics.ScanFaceBiometricsViewModel;
import com.covrsecurity.io.ui.viewmodel.scanfacebiometrics.ScanFaceBiometricsViewModelFactory;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FileUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.domain.entity.request.RegisterRecoveryRequestEntity;
import com.covrsecurity.io.utils.IamTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;

import co.hyperverge.hypersnapsdk.HyperSnapSDK;
import co.hyperverge.hypersnapsdk.activities.HVDocsActivity;
import co.hyperverge.hypersnapsdk.activities.HVFaceActivity;
import co.hyperverge.hypersnapsdk.listeners.DocCaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.listeners.FaceCaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.objects.HVDocConfig;
import co.hyperverge.hypersnapsdk.objects.HVError;
import co.hyperverge.hypersnapsdk.objects.HVFaceConfig;
import co.hyperverge.hypersnapsdk.objects.HyperSnapParams;
import timber.log.Timber;

public class ScanFaceBiometricsFragment extends
//        BaseUnauthorizedFragment<FragmentScanFaceBiometricsBinding>
        BaseScanFaceBiometricsFragment<FragmentScanFaceBiometricsBinding, ScanFaceBiometricsViewModel>
{

//    @Deprecated
    public static final String CAPTURED_IMAGE_JPEG = "CAPTURED_IMAGE_JPEG.jpeg";
    public static final String CAPTURED_DOCUMENT_JPEG = "CAPTURED_DOCUMENT_JPEG.jpeg";

    private static final String SCAN_REASON_EXTRA = "SCAN_REASON_EXTRA";
    private static final String RECOVERY_ID_EXTRA = "RECOVERY_ID_EXTRA";
    private static final String COVR_CODE_EXTRA = "COVR_CODE_EXTRA";
    private static final String TRANSACTION_EXTRA = "TRANSACTION_EXTRA";
    private static final String ACCEPT_EXTRA = "ACCEPT_EXTRA";
    public static final String SETUP_RECOVERY = "SETUP_RECOVERY";

    private static final String HYPER_SNAP_APP_ID = "9a6cc4";
    private static final String HYPER_SNAP_APP_KEY = "15df1efdb58328becfac";

    private String selfie = null;
    private String documentFrontSide = null;
    private File file = null;
    private com.covrsecurity.io.domain.entity.request.RegisterRecoveryRequestEntity registerRecovery;

    private FaceCaptureCompletionHandler myCaptureCompletionListener;

    public ScanFaceBiometricsFragment() {}

    public static ScanFaceBiometricsFragment newInstance() {
        Bundle args = new Bundle();
        args.putSerializable(SCAN_REASON_EXTRA, ScanIntention.POST_RECOVERY_FROM_SETTINGS);
        return getScanFaceBiometricsFragment(args);
    }

    public static ScanFaceBiometricsFragment newInstance(String recoveryId) {
        Bundle args = new Bundle();
        args.putSerializable(SCAN_REASON_EXTRA, ScanIntention.RECOVERY);
        args.putString(RECOVERY_ID_EXTRA, recoveryId);
        return getScanFaceBiometricsFragment(args);
    }

    public static ScanFaceBiometricsFragment newInstance(char[] covrCode) {
        Bundle args = new Bundle();
        args.putSerializable(SCAN_REASON_EXTRA, ScanIntention.REGISTRATION);
        args.putCharArray(COVR_CODE_EXTRA, covrCode);
        return getScanFaceBiometricsFragment(args);
    }

    public static ScanFaceBiometricsFragment newInstance(TransactionEntity transaction, boolean accept) {
        Bundle args = new Bundle();
        args.putSerializable(SCAN_REASON_EXTRA, ScanIntention.VERIFICATION);
        args.putSerializable(TRANSACTION_EXTRA, transaction);
        args.putBoolean(ACCEPT_EXTRA, accept);
        return getScanFaceBiometricsFragment(args);
    }

    private static ScanFaceBiometricsFragment getScanFaceBiometricsFragment(Bundle args) {
        ScanFaceBiometricsFragment fragment = new ScanFaceBiometricsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ScanFaceBiometricsViewModelFactory vmFactory;
    @Inject
    BiometricsSharedViewModelFactory sharedVmFactory;

    @Nullable
    private BiometricsSharedViewModel sharedViewModel;

    private ScanIntention mScanReason;
    @Nullable
    private String recoveryId;
    @Nullable
    private char[] mEnteredText;
    @Nullable
    private TransactionEntity mTransaction;
    private boolean mAccept;
    @Nullable
    private AlertDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scan_face_biometrics;
    }

    @Override
    public boolean usesBottomButtons() {
        return true;
    }

    @Override
    public boolean isRegistration() {
        return mScanReason != ScanIntention.RECOVERY;
    }

    @NonNull
    @Override
    protected Class<ScanFaceBiometricsViewModel> getViewModelClass() {
        return ScanFaceBiometricsViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof AuthorizedActivity) {
            sharedViewModel = ViewModelProviders.of(getActivity(), sharedVmFactory).get(BiometricsSharedViewModel.class);
        }
        viewModel.recoverLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    // TODO Analytics.logEvent
                    final Fragment fragment = CreatePersonalCodeFragment.newInstance(CreatePersonalCodeFragment.CreateCodeIntention.RECOVER);
                    replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
                },
                throwable -> {
                    hideProgress();
                    Timber.e(throwable);
                    showErrToast(throwable);
                }
        ));
        viewModel.registerLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                    Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_CODE_CREATE);
                    final Fragment fragment = RegisterBiometricRecoveryFragment.getInstance(mEnteredText, selfie, documentFrontSide);
                    replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
//                    final boolean registration = isRegistration();
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
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        final Bundle arguments = getArguments();
        mScanReason = (ScanIntention) arguments.getSerializable(SCAN_REASON_EXTRA);
        recoveryId = arguments.getString(RECOVERY_ID_EXTRA);
        mEnteredText = arguments.getCharArray(COVR_CODE_EXTRA);
        mTransaction = (TransactionEntity) arguments.getSerializable(TRANSACTION_EXTRA);
        mAccept = arguments.getBoolean(ACCEPT_EXTRA);
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindToLifecycle();
        } else {
            requestPermissions(ConstantsUtils.CAMERA_PERMISSION, ConstantsUtils.CAMERA_REQUEST_CODE);
        }
        mBinding.next.setVisibility(View.GONE);
        mBinding.capture.setOnClickListener(view1 -> {
            if ((getContext() != null || getActivity() != null) && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                mBinding.capture.setEnabled(true);
                HyperSnapSDK.init(getContext(), HYPER_SNAP_APP_ID, HYPER_SNAP_APP_KEY, HyperSnapParams.Region.India);
                if ((mScanReason == ScanIntention.VERIFICATION
                        || (mScanReason == ScanIntention.REGISTRATION && mBinding.capture.getText().equals(getString(R.string.capture)))
                        || mScanReason == ScanIntention.POST_RECOVERY_FROM_SETTINGS
                        || mScanReason == ScanIntention.RECOVERY)
                        || (mScanReason == ScanIntention.SCAN_DOCUMENT_FRONT_SIDE && mBinding.capture.getText().equals(getString(R.string.recapture)))) {
                    captureHyperSnap();
                } else {
                    mScanReason = ScanIntention.SCAN_DOCUMENT_FRONT_SIDE;
                    documentCaptureHyperSnap();
                    mBinding.capture.setText(R.string.capture);
                    mBinding.captureImage.setImageDrawable(null);
                    mBinding.captureImage.setVisibility(View.GONE);
                    enableNextButton(false);
                }
            } else {
                requestPermissions(ConstantsUtils.CAMERA_PERMISSION, ConstantsUtils.CAMERA_REQUEST_CODE);
            }
        });
        mBinding.closeButton.setOnClickListener(close -> onBackButton());
        mBinding.backButton.setOnClickListener(view1 -> ((UnauthorizedActivity) Objects.requireNonNull(getActivity())).goBack());
    }

    private void captureHyperSnap() {
         myCaptureCompletionListener = new FaceCaptureCompletionHandler() {
            @Override
            public void onResult(HVError error, JSONObject result, JSONObject headers) {
                if (error != null && getActivity() != null) { /*Handle error*/
                    Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        selfie = (String) result.get("imageUri");
                        file = new File(selfie);
                        if (!selfie.equals("")) {
                            if (mScanReason == ScanIntention.POST_RECOVERY_FROM_SETTINGS) {
                                documentCaptureHyperSnap();
                            } else {
                                bindToLifecycle();
                                if (mScanReason != ScanIntention.RECOVERY && mScanReason != ScanIntention.VERIFICATION) {
                                    mScanReason = ScanIntention.SCAN_DOCUMENT_FRONT_SIDE;
                                }
                            }
                        } else {
                            AuthorizedActivity.hideLockScreen = true;
                            onBackPressed();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        int selfieId = 1111;
        HVFaceConfig hvFaceConfig = new HVFaceConfig();
        hvFaceConfig.setShouldEnablePadding(true);
        JSONObject livenessHeader = new JSONObject();
        JSONObject livenessParams = new JSONObject();
        try {
            livenessHeader.put("transactionId", selfieId /*Unique ID for customer - for billing*/);
            livenessParams.put("enableDashboard", "yes");//This facilitates access to QC dashboard and debugging during POC }catch (JSONException e) { }
            hvFaceConfig.setLivenessAPIHeaders(livenessHeader);
            hvFaceConfig.setLivenessAPIParameters(livenessParams);
            if (getActivity() != null) {
                HVFaceActivity.start(getActivity(), hvFaceConfig, myCaptureCompletionListener);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void documentCaptureHyperSnap() {
        DocCaptureCompletionHandler myCaptureCompletionListener = new DocCaptureCompletionHandler() {
            @Override
            public void onResult(HVError error, JSONObject result) {
                if(error != null) {/*Handle error*/
                    Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    mBinding.capture.setEnabled(true);
                } else { /*Handle result*/
                    try {
                        documentFrontSide = (String) result.get("imageUri");
                        file = new File(documentFrontSide);
                        if (!documentFrontSide.equals("")) {
                            bindToLifecycle();
                        } else {
                            AuthorizedActivity.hideLockScreen = true;
                            onBackPressed();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        HVDocConfig hvDocConfig = new HVDocConfig();
        HVDocConfig.Document document = HVDocConfig.Document.OTHER;
        document.setAspectRatio(0.625f);
        hvDocConfig.setDocumentType(document);
        hvDocConfig.setShouldShowReviewScreen(false);
        hvDocConfig.setPadding(0.05f);
        HVDocsActivity.start(getContext(), hvDocConfig, myCaptureCompletionListener);
    }

    private void bindToLifecycle() {
//        mBinding.cameraPreview.setLifecycleOwner(this);
//        mBinding.cameraPreview.clearCameraListeners();
        if (file != null) {
            AuthorizedActivity.hideLockScreen = true;
            if (sharedViewModel != null) {
                ActivityUtils.scheduleOnMainThread(() -> {
                    if (mScanReason == ScanIntention.VERIFICATION) {
                        BiometricsVerification biometricsVerification = null;
                        try {
                            biometricsVerification = new BiometricsVerification(mTransaction, FileUtils.readAllBytes(getImageFile(selfie)), mAccept);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sharedViewModel.sharedLiveData.postValue(BaseState.getSuccessInstance(biometricsVerification));
                    } else if (mScanReason == ScanIntention.POST_RECOVERY_FROM_SETTINGS) {
//                        RegisterRecoveryRequestEntity registerRecovery = null;
                        try {
                            registerRecovery = new RegisterRecoveryRequestEntity(FileUtils.readAllBytes(getImageFile(selfie)), FileUtils.readAllBytes(getImageFile(documentFrontSide)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sharedViewModel.registerRecoveryLiveData.postValue(BaseState.getSuccessInstance(registerRecovery));
                        final Fragment fragment = SettingsFragment.newInstance();
                        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
                    }
                }, ConstantsUtils.TWO_HUNDRED_MILLISECONDS);
            } else {
                try {
                    FileUtils.rotateImage(file);

                    mBinding.descriptionSection.setVisibility(View.GONE);
                    mBinding.capture.setEnabled(true);
                    mBinding.captureImage.setImageDrawable(null);
                    mBinding.captureImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    mBinding.capture.setText(R.string.recapture);
                    mBinding.captureImage.setVisibility(View.VISIBLE);

                    if (mScanReason != ScanIntention.RECOVERY) {
                        mScanReason = ScanIntention.REGISTRATION;
                    }
                    mBinding.next.setVisibility(View.VISIBLE);
                    mBinding.next.setOnClickListener(view -> nextClick());
                    enableNextButton(true);
                } catch (IOException e) {
                    Timber.w(e);
                    showErrToast(e);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindToLifecycle();
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            mDialog = DialogUtils.showAlertDialog(
                    getActivity(),
                    getString(R.string.сamera_permission_not_granted),
                    getString(R.string.open_settings_to_grant_permission_to_take_biometrics),
                    getString(R.string.open_settings),
                    (dialogInterface, i) -> openApplicationSettings(),
                    getString(R.string.close_app),
                    (dialogInterface, i) -> finishActivity(),
                    false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.CAMERA_REQUEST_CODE) {
            bindToLifecycle();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    @Override
//    protected void onNextButtonClick() {
//        super.onNextButtonClick();
//        if (ScanIntention.RECOVERY == mScanReason) {
//            try {
//                viewModel.recover(recoveryId, FileUtils.readAllBytes(getImageFile(selfie)));
//            } catch (IOException e) {
//                Timber.e(e);
//            }
//        } else if (ScanIntention.REGISTRATION == mScanReason) {
////            try {
////                viewModel.register(mEnteredText, true, FileUtils.readAllBytes(getImageFile(selfie)), FileUtils.readAllBytes(getImageFile(documentFrontSide)));
////            } catch (IOException e) {
////                Timber.e(e);
////            }
//        } else if (ScanIntention.SCAN_DOCUMENT_FRONT_SIDE == mScanReason) {
//            documentCaptureHyperSnap();
//            mBinding.capture.setText(R.string.capture);
//            mBinding.captureImage.setImageDrawable(null);
////            mBinding.cameraPreview.setVisibility(View.GONE);
//            mBinding.captureImage.setVisibility(View.GONE);
//            enableNextButton(false);
//        }
//    }

    private void nextClick() {
        if (ScanIntention.RECOVERY == mScanReason) {
            try {
                viewModel.recover(recoveryId, FileUtils.readAllBytes(getImageFile(selfie)));
            } catch (IOException e) {
                Timber.e(e);
            }
        } else if (ScanIntention.REGISTRATION == mScanReason) {
//            try {
//                viewModel.register(mEnteredText, true, FileUtils.readAllBytes(getImageFile(selfie)), FileUtils.readAllBytes(getImageFile(documentFrontSide)));
//            } catch (IOException e) {
//                Timber.e(e);
//            }
            startAuthorizedActivity(true);
        } else if (ScanIntention.SCAN_DOCUMENT_FRONT_SIDE == mScanReason) {
            documentCaptureHyperSnap();
            mBinding.capture.setText(R.string.capture);
            mBinding.captureImage.setImageDrawable(null);
            mBinding.captureImage.setVisibility(View.GONE);
            enableNextButton(false);
        }
    }

    protected void startAuthorizedActivity(final boolean isAfterRegistration) {
        IamTools.startAuthorizedActivity(getActivity(), isAfterRegistration);
    }

    @Override
    public void onKeyboardBackPressed() {
        if (sharedViewModel != null && mTransaction != null) {
            sharedViewModel.sharedLiveData.postValue(BaseState.getErrorInstance(new BioMetricDataProvideCancel(mTransaction.getId())));
        }
    }

    private void openApplicationSettings() {
        String uriString = String.format(ConstantsUtils.APP_SETTINGS_TEMPLATE, getActivity().getPackageName());
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(uriString));
        startActivityForResult(appSettingsIntent, ConstantsUtils.CAMERA_REQUEST_CODE);
    }

    private File getImageFile(String image) {
        return new File(image);
    }

    private enum ScanIntention {
        RECOVERY,
        POST_RECOVERY_FROM_SETTINGS,
        REGISTRATION,
        VERIFICATION,
        SCAN_DOCUMENT_FRONT_SIDE,
    }
}
