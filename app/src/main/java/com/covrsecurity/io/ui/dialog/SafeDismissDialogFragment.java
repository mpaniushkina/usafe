package com.covrsecurity.io.ui.dialog;

import android.content.DialogInterface;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public abstract class SafeDismissDialogFragment extends DialogFragment {

    private volatile boolean mDialogDismissed;
    protected OnDismissListener mDismissListener;

    public void setDismissListener(FingerprintAuthenticationFailedDialogFragment.OnDismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDialogDismissed && getDialog() != null) {
            getDialog().dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    @Override
    public void dismiss() {
        if (isResumed()) {
            super.dismiss();
        } else {
            mDialogDismissed = true;
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (isResumed()) {
            super.dismissAllowingStateLoss();
        } else {
            mDialogDismissed = true;
        }
    }

    public void show(FragmentManager manager) {
        super.show(manager, provideTag());
    }

    protected abstract String provideTag();

    public interface OnDismissListener {
        void onDismiss();
    }
}
