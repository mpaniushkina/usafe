package com.covrsecurity.io.ui.fragment.unauthorized;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentStartQrCodeBinding;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.dialog.ScanQrCodeDialog;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;

public class ScanQrCodeFragment extends BaseUnauthorizedFragment<FragmentStartQrCodeBinding> {

    public static ScanQrCodeFragment newInstance() {
        return new ScanQrCodeFragment();
    }

    @Nullable
    private AlertDialog mDialog;
    @Nullable
    private ScanQrCodeDialog mScanQrCodeDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_start_qr_code;
    }

    @Override
    public boolean isRegistration() {
        return false;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setScanClickListener(v -> {
            if (!isCameraPermissionsGranted()) {
                requestCameraPermissions();
            } else {
                sendScanIntent();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        UnauthorizedActivity unauthorizedActivity = getUnauthorizedActivity();
        if (unauthorizedActivity == null) {
            return;
        }
        unauthorizedActivity.showButtons();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mScanQrCodeDialog != null) {
            mScanQrCodeDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.SCAN_QR_REQUEST_CODE && Activity.RESULT_OK == resultCode) {
            final String qrCode = ScanQrCodeDialog.parseQrCodeResult(data);
            ActivityUtils.scheduleOnMainThread(
                    () -> moveToScanFaceBiometricsFragment(qrCode),
                    ConstantsUtils.FOUR_HUNDRED_MILLISECONDS
            );
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantsUtils.CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendScanIntent();
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                mDialog = DialogUtils.showAlertDialog(
                        getActivity(),
                        getString(R.string.сamera_permission_not_granted),
                        getString(R.string.open_settings_to_grant_permission_to_take_qr),
                        getString(R.string.open_settings),
                        (dialogInterface, i) -> openApplicationSettings(),
                        getString(R.string.close_app),
                        (dialogInterface, i) -> finishActivity(),
                        false);
            } else {
                showToast(R.string.camera_permission_declined);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isCameraPermissionsGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(getActivity(), ConstantsUtils.CAMERA_PERMISSION, ConstantsUtils.CAMERA_REQUEST_CODE);
    }

    private void sendScanIntent() {
        final ScanQrCodeDialog scanQrCodeDialog = new ScanQrCodeDialog();
        scanQrCodeDialog.show(getFragmentManager(), ScanQrCodeDialog.QR_CODE_TAG);
        scanQrCodeDialog.setTargetFragment(this, ConstantsUtils.SCAN_QR_REQUEST_CODE);
        mScanQrCodeDialog = scanQrCodeDialog;
    }

    private void openApplicationSettings() {
        String uriString = String.format(ConstantsUtils.APP_SETTINGS_TEMPLATE, getActivity().getPackageName());
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(uriString));
        startActivityForResult(appSettingsIntent, ConstantsUtils.CAMERA_REQUEST_CODE);
    }

    private void moveToScanFaceBiometricsFragment(String qrCode) {
        final ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance(qrCode);
        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
    }

    @Override
    public boolean usesBottomButtons() {
        return false;
    }
}
