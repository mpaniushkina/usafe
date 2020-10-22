package com.covrsecurity.io.utils;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.sdk.exception.ApiNetworkException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.ApiException;

import java.io.IOError;
import java.io.IOException;
import java.net.ConnectException;

public class ErrorMsgHelper {

    private static final int MAX_ERR_LEN = 5;
    private AlertDialog noInternetDialog;
    private AlertDialog sessionConflictDialog;

    private static String BAD_DECRYPT_TAG =  "BAD_DECRYPT";

    public void showErrDlg(Activity activity, Throwable t) {
        showErrDlg(activity, t, null);
    }

    public void showErrDlg(Activity activity, Throwable t, DialogInterface.OnClickListener listener) {
        if (activity != null) {
            if (!checkGoogleMsg(activity, t)) {
                String msg = checkConnectionMsg(t);
                if (msg != null && !msg.isEmpty()) {
                    showOkDialog(activity, msg, listener);
                }
            }
        }
    }

    private void showOkDialog(Activity activity, String msg) {
        showOkDialog(activity, msg, null);
    }

    public void showOkDialog(Activity activity, String msg, DialogInterface.OnClickListener listener) {
        ActivityUtils.runOnMainThread(() -> {
            dismiss();
            noInternetDialog = DialogUtils.showOkDialog(activity, msg, false, listener);
        });
    }

    public void showErrToast(Throwable t) {
        String msg = checkConnectionMsg(t);
        showToast(msg);
    }

    private boolean checkGoogleMsg(Activity activity, Throwable t) {
        if (t instanceof GooglePlayServicesNotAvailableException) {
            int errorCode = ((GooglePlayServicesNotAvailableException) t).errorCode;
            if (GoogleApiAvailability.getInstance().isUserResolvableError(errorCode)) {
                if (activity != null) {
                    PlayServicesUtils.checkPlayServicesAndShowErrorDialog(activity);
                }
            }
            return true;
        }
        return false;
    }

    private String checkConnectionMsg(Throwable t) {
        String msg = t.getMessage();
        if (t instanceof NetworkErrorException || t instanceof ConnectException
                || t instanceof ApiException) {
            msg = AppAdapter.resources().getString(R.string.error_server);
        } else if (t instanceof ApiNetworkException) {
            msg = ((ApiNetworkException) t).getDescription();
        } else if (t instanceof IOException || t instanceof IOError) {
            msg = AppAdapter.resources().getString(R.string.error_no_internet);
        } else {
            msg = checkMsg(msg);
        }

        return msg;
    }

    public void dismiss() {
        if (noInternetDialog != null) {
            noInternetDialog.dismiss();
        }
        if (sessionConflictDialog != null) {
            sessionConflictDialog.dismiss();
        }
    }

    public void showNoInternetDialog(Activity activity) {
        if (!isNoInternetDialogShowing()) {
            noInternetDialog = DialogUtils.showOkDialog(activity, AppAdapter.resources().getString(R.string.error_no_internet_title),
            AppAdapter.resources().getString(R.string.error_no_internet), null, null, true);
        }
    }

    public void showServerErrorDialog(Activity activity) {
        if (!isNoInternetDialogShowing()) {
            noInternetDialog = DialogUtils.showOkDialog(activity, AppAdapter.resources().getString(R.string.error_server_title),
            AppAdapter.resources().getString(R.string.error_server), null, null, false);
        }
    }

    public void showUnknownErrorDialog(Activity activity, String message) {
        if (!isNoInternetDialogShowing()) {
            noInternetDialog = DialogUtils.showOkDialog(activity,
            AppAdapter.resources().getString(R.string.error_unknown), message,
                    null, null, true);
        }
    }

    public void showSessionConflictErrorDialog(Activity activity, DialogInterface.OnClickListener listener) {
        if (!isSessionConflictDialogShowing()) {
            sessionConflictDialog = DialogUtils.showOkDialog(activity, null,
            AppAdapter.resources().getString(R.string.error_unauthorized),
                    null, listener, false);
        }
    }

    public void showNoInternetDlg(Activity activity, DialogInterface.OnClickListener listener) {
        String msg = AppAdapter.resources().getString(R.string.error_no_internet);
        showOkDialog(activity, msg);
    }

    private boolean isNoInternetDialogShowing() {
        return noInternetDialog != null && noInternetDialog.isShowing();
    }

    private boolean isSessionConflictDialogShowing() {
        return sessionConflictDialog != null && sessionConflictDialog.isShowing();
    }

    private String checkMsg(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (msg.contains(BAD_DECRYPT_TAG)) {
                return AppAdapter.resources().getString(R.string.error_server);
            } else if (msg.length() > MAX_ERR_LEN) {
                return msg;
            }
        }
        return "";
    }

    public void showToast(int msgId) {
        String msg = AppAdapter.resources().getString(msgId);
        showToast(msg);
    }

    public void showToast(String msg) {
        ActivityUtils.scheduleOnMainThread(() -> Toast.makeText(AppAdapter.context(), msg, Toast.LENGTH_SHORT).show());
    }
}
