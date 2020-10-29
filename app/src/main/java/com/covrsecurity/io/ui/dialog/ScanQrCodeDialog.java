package com.covrsecurity.io.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.covrsecurity.io.R;
import com.covrsecurity.io.utils.ConstantsUtils;

import timber.log.Timber;

public class ScanQrCodeDialog extends DialogFragment {

    public static final String QR_CODE_TAG = "QR_CODE_TAG";

    private static final String QR_CODE_EXTRA = "QR_CODE_EXTRA";

    public static String parseQrCodeResult(Intent data) {
        return data.getStringExtra(QR_CODE_EXTRA);
    }

    private CodeScanner mCodeScanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_scan_qr, container, false);
        final CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        ImageView doneQrScan = root.findViewById(R.id.doneQrScan);
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.setDecodeCallback(result -> scannerView.post(() -> {
            final String text = result.getText();
            Timber.d(text);
            final Intent data = new Intent();
            data.putExtra(QR_CODE_EXTRA, text);
            doneQrScan.setVisibility(View.VISIBLE);
            getTargetFragment().onActivityResult(ConstantsUtils.SCAN_QR_REQUEST_CODE, Activity.RESULT_OK, data);
            final Handler handler = new Handler();
            final Runnable r = () -> dismiss();
            handler.postDelayed(r, 1500);
        }));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
