package com.covrsecurity.io.ui.fragment.authorized;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentPendingDetailsBinding;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.model.TimerInfo;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.adapter.PendingRequestsAdapter;
import com.covrsecurity.io.ui.component.CircleTimer;
import com.covrsecurity.io.ui.component.CovrCircleTimer;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.utils.SoundUtils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class PendingDetailsFragment extends BaseFragment<FragmentPendingDetailsBinding> {

    private static final String PENDING_TRANSACTION = "PENDING_TRANSACTION";
    private static final String CURRENT_TIME_EXTRA = "CURRENT_TIME_EXTRA";
    private static final String FORMAT = "%02d:%02d";

    private TransactionEntity transactionDetails;
    private long currentTime;
    private boolean isTimerExpire;

    public static Fragment newInstance(TransactionEntity pendingTransaction, long currentTime) {
        Fragment fragment = new PendingDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PENDING_TRANSACTION, pendingTransaction);
        args.putLong(CURRENT_TIME_EXTRA, currentTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pending_details;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (getArguments() != null) {
            transactionDetails = (TransactionEntity) getArguments().getSerializable(PENDING_TRANSACTION);
            currentTime = getArguments().getLong(CURRENT_TIME_EXTRA);
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.transaction_details_title);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());
        mBinding.pendingTitle.setText(transactionDetails.getRequest().getTitle());
        mBinding.transactionOverlayMessageText.setText(transactionDetails.getRequest().getMessage());
        mBinding.positiveButton.setText(transactionDetails.getRequest().getAccept().length() > 0 ?
                transactionDetails.getRequest().getAccept() : getString(R.string.pending_request_verify));
        mBinding.negativeButton.setText(transactionDetails.getRequest().getReject().length() > 0 ?
                transactionDetails.getRequest().getReject() : getString(R.string.pending_request_deny));
        mBinding.timerText.setVisibility(View.VISIBLE);

        new CountDownTimer(currentTime, 1000) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {

                isTimerExpire = false;
                if (getContext() != null) {
                    mBinding.timerText.setText(getContext().getString(R.string.timer_expires) + String.format(FORMAT,
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }
            }

            public void onFinish() {
                isTimerExpire = true;
                mBinding.timerText.setVisibility(View.GONE);
                mBinding.positiveButton.setBackgroundResource(R.drawable.button_green_disabled);
                mBinding.negativeButton.setBackgroundResource(R.drawable.button_red_disabled);
                mBinding.transactionExpireText.setVisibility(View.VISIBLE);
            }
        }.start();

        StandingByFragment fragment = (StandingByFragment) getFragmentManager().findFragmentByTag(StandingByFragment.class.getName());
        mBinding.negativeButton.setOnClickListener(v -> {
            if (fragment != null && !isTimerExpire) {
                AuthorizedActivity.hideLockScreen = true;
                fragment.postTransaction(transactionDetails, false);
                onBackPressed();
            }
        });
        mBinding.positiveButton.setOnClickListener(v -> {
            if (fragment != null && !isTimerExpire) {
                AuthorizedActivity.hideLockScreen = true;
                fragment.postTransaction(transactionDetails, true);
                onBackPressed();
            }
        });
    }
}
