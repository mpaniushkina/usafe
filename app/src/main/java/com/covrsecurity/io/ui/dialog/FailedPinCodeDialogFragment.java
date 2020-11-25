package com.covrsecurity.io.ui.dialog;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.DialogPinCodeFailedBinding;
import com.covrsecurity.io.model.databinding.FailedLoginDialogModel;

import org.jetbrains.annotations.NotNull;

public class FailedPinCodeDialogFragment extends SafeDismissDialogFragment {

    private static final String DIALOG_FAILED_PIN_TAG = "com.covrsecurity.io.ui.dialog.FailedPinCodeDialogFragment.DIALOG_FAILED_PIN_TAG";
    private static final String ATTEMPTS_LEFT_EXTRA = "ATTEMPTS_LEFT_EXTRA";
    private static final String IS_WRONG_PIN_EXTRA = "IS_WRONG_PIN_EXTRA";
    private static final String CANCELABLE_EXTRA = "CANCELABLE_EXTRA";

    public static FailedPinCodeDialogFragment getInstance(int attemptsLeft, boolean isWrongPin, boolean cancelable) {
        Bundle args = getBundleArgs(attemptsLeft, isWrongPin, cancelable);
        FailedPinCodeDialogFragment dialogFragment = new FailedPinCodeDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NotNull
    private static Bundle getBundleArgs(int attemptsLeft, boolean isWrongPin, boolean cancelable) {
        Bundle args = new Bundle();
        args.putInt(ATTEMPTS_LEFT_EXTRA, attemptsLeft);
        args.putBoolean(IS_WRONG_PIN_EXTRA, isWrongPin);
        args.putBoolean(CANCELABLE_EXTRA, cancelable);
        return args;
    }

    private DialogInterface.OnClickListener mOnClickListener;
    private FailedLoginDialogModel mLoginDialogModel;

    @Override
    protected String provideTag() {
        return DIALOG_FAILED_PIN_TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            setCancelable(arguments.getBoolean(CANCELABLE_EXTRA));
            final int attemptsLeft = arguments.getInt(ATTEMPTS_LEFT_EXTRA);
            final boolean isWrongPin = arguments.getBoolean(IS_WRONG_PIN_EXTRA, true);
            mLoginDialogModel = new FailedLoginDialogModel(attemptsLeft, isWrongPin);
            mLoginDialogModel.setTryAgainText(getString(R.string.ok));
        } else {
            setCancelable(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        DialogPinCodeFailedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(inflater.getContext()), R.layout.dialog_pin_code_failed, null, false);
        binding.setDialogModel(mLoginDialogModel);
        binding.okText.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
