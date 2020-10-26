package com.covrsecurity.io.ui.fragment.authorized;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentAddConnectionBinding;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.dialog.ScanQrCodeDialog;
import com.covrsecurity.io.ui.fragment.BaseChildViewModelFragment;
import com.covrsecurity.io.ui.viewmodel.addconnection.AddConnectionViewModel;
import com.covrsecurity.io.ui.viewmodel.addconnection.AddConnectionViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.utils.ConstantsUtils;

import javax.inject.Inject;

public class AddConnectionFragment extends BaseChildViewModelFragment<FragmentAddConnectionBinding, AddConnectionViewModel> {

    public static final String QR_CODE_EXTRA = "QR_CODE_EXTRA";

    public static Fragment newInstance() {
        return new AddConnectionFragment();
    }

    public static Fragment newInstance(String qrCode) {
        final Fragment fragment = new AddConnectionFragment();
        final Bundle args = new Bundle();
        args.putString(QR_CODE_EXTRA, qrCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    AddConnectionViewModelFactory vmFactory;

    @Nullable
    private ScanQrCodeDialog mScanQrCodeDialog;

    @Nullable
    private String mQrCode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_connection;
    }

    @NonNull
    @Override
    protected Class<AddConnectionViewModel> getViewModelClass() {
        return AddConnectionViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mQrCode = arguments.getString(QR_CODE_EXTRA);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.addConnectionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();

                    Fragment fragment = ConnectionEstablishedFragment.newInstance(
                            response.getCompany().getLogo(),
                            response.getCompany().getFullName()
                    );

                    showChildFragment(
                            fragment,
                            fragment.getArguments(),
                            false,
                            R.animator.right_slide_in,
                            R.animator.left_slide_in,
                            R.animator.fade_in,
                            R.animator.left_slide_in
                    );
                },
                throwable -> {
                    hideProgress();
                    showError(throwable.getMessage());
                }
        ));
        if (!TextUtils.isEmpty(mQrCode)) {
            viewModel.addConnection(mQrCode);
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setCloseClickListener(v -> closeChildFragment());
        mBinding.setScanClickListener(v -> {
            if (!isCameraPermissionsGranted()) {
                requestCameraPermissions();
            } else {
                sendScanIntent();
            }
        });
        mBinding.addConnectionError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.SCAN_QR_REQUEST_CODE && Activity.RESULT_OK == resultCode) {
            final String qrCode = ScanQrCodeDialog.parseQrCodeResult(data);
            viewModel.addConnection(qrCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantsUtils.CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendScanIntent();
            } else {
                showToast(R.string.camera_permission_declined);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mScanQrCodeDialog != null) {
            mScanQrCodeDialog.dismissAllowingStateLoss();
        }
    }

    private void showError(String message) {
        mBinding.addConnectionError.setVisibility(View.VISIBLE);
        mBinding.addConnectionError.setText(message);
    }

    private boolean isCameraPermissionsGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions() {
        if (this.getParentFragment() instanceof StandingByFragment) {
            ((AuthorizedActivity) getActivity()).setShouldAddConnectionShown(true);
        }
        ActivityCompat.requestPermissions(getActivity(), ConstantsUtils.CAMERA_PERMISSION, ConstantsUtils.CAMERA_REQUEST_CODE);
    }

    private void sendScanIntent() {
        final ScanQrCodeDialog scanQrCodeDialog = new ScanQrCodeDialog();
        scanQrCodeDialog.show(getFragmentManager(), ScanQrCodeDialog.QR_CODE_TAG);
        scanQrCodeDialog.setTargetFragment(this, ConstantsUtils.SCAN_QR_REQUEST_CODE);
        mScanQrCodeDialog = scanQrCodeDialog;
    }
}