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
import com.covrsecurity.io.databinding.DialogLoginFailedBinding;
import com.covrsecurity.io.model.databinding.FailedLoginDialogModel;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.instacart.library.truetime.TrueTime;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class FailedLoginDialogFragment extends SafeDismissDialogFragment {

    private static final String DIALOG_FAILED_LOGIN_TAG = "com.covrsecurity.io.ui.dialog.FailedLoginDialogFragment.DIALOG_FAILED_LOGIN_TAG";
    private static final String ATTEMPTS_LEFT_EXTRA = "ATTEMPTS_LEFT_EXTRA";
    private static final String UNLOCK_TIME_EXTRA = "UNLOCK_TIME_EXTRA";
    private static final String IS_LOGIN_EXTRA = "IS_LOGIN_EXTRA";
    private static final String CANCELABLE_EXTRA = "CANCELABLE_EXTRA";
    private static final DateFormat FORMAT_DATE = new SimpleDateFormat(ConstantsUtils.FORMAT_DATE_HH_MM_SS_SRT);

    public static FailedLoginDialogFragment getInstance(int attemptsLeft, long unlockTime, boolean isLogin, boolean cancelable) {
        Bundle args = getBundleArgs(attemptsLeft, unlockTime, isLogin, cancelable);
        FailedLoginDialogFragment dialogFragment = new FailedLoginDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NotNull
    private static Bundle getBundleArgs(int attemptsLeft, long unlockTime, boolean isLogin, boolean cancelable) {
        Bundle args = new Bundle();
        args.putInt(ATTEMPTS_LEFT_EXTRA, attemptsLeft);
        args.putLong(UNLOCK_TIME_EXTRA, unlockTime);
        args.putBoolean(IS_LOGIN_EXTRA, isLogin);
        args.putBoolean(CANCELABLE_EXTRA, cancelable);
        return args;
    }

    private DialogInterface.OnClickListener mOnClickListener;
    private FailedLoginDialogModel mLoginDialogModel;
    @Nullable
    private Timer mTimer;
    @Nullable
    private TimerTask mTimerTask;

    @Override
    protected String provideTag() {
        return DIALOG_FAILED_LOGIN_TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        }
        FORMAT_DATE.setTimeZone(TimeZone.getTimeZone("GMT"));
        Bundle arguments = getArguments();
        if (arguments != null) {
            setCancelable(arguments.getBoolean(CANCELABLE_EXTRA));
            final int attemptsLeft = arguments.getInt(ATTEMPTS_LEFT_EXTRA);
            final boolean isLogin = arguments.getBoolean(IS_LOGIN_EXTRA, true);
            final long unlockTime = arguments.getLong(UNLOCK_TIME_EXTRA);
            mLoginDialogModel = new FailedLoginDialogModel(attemptsLeft, isLogin);
            if (unlockTime > TrueTime.now().getTime()) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (!isAdded()) {
                            return;
                        }
                        long remainLockTime = unlockTime - System.currentTimeMillis(); // TODO TrueTime.now().getTime();
                        if (remainLockTime > ConstantsUtils.MILLISECONDS_IN_SECOND) {
                            String remainLockTimeFormat = FORMAT_DATE.format(new Date(remainLockTime));
                            mLoginDialogModel.setTryAgainText(getString(R.string.failed_attempts_try_again_in, remainLockTimeFormat));
                            mLoginDialogModel.setIsTryAgainClickable(false);
                        } else {
                            mLoginDialogModel.setTryAgainText(getString(R.string.ok).toUpperCase());
                            mLoginDialogModel.setIsTryAgainClickable(true);
                            cancelTimer();
                        }
                    }
                };
                mTimer = new Timer();
                mTimer.schedule(mTimerTask, 0, ConstantsUtils.MILLISECONDS_IN_SECOND);
            } else {
                mLoginDialogModel.setTryAgainText(getString(R.string.ok).toUpperCase());
                mLoginDialogModel.setIsTryAgainClickable(true);
            }
        } else {
            setCancelable(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        DialogLoginFailedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(inflater.getContext()), R.layout.dialog_login_failed, null, false);
        binding.setDialogModel(mLoginDialogModel);
        binding.tryAgainText.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        cancelTimer();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        cancelTimer();
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
    }
}
