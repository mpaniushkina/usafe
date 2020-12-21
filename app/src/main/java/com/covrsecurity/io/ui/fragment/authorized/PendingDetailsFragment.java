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

    private TransactionEntity transactionDetails;

    private static final String FORMAT = "%02d:%02d:%02d";
    int seconds , minutes;

    public static Fragment newInstance(TransactionEntity pendingTransaction) {
        Fragment fragment = new PendingDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PENDING_TRANSACTION, pendingTransaction);
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
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText("");
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());
        mBinding.pendingTitle.setText(transactionDetails.getRequest().getTitle());
        mBinding.transactionOverlayMessageText.setText(transactionDetails.getRequest().getMessage());
        mBinding.positiveButton.setText(transactionDetails.getRequest().getAccept().length() > 0 ?
                transactionDetails.getRequest().getAccept() : getString(R.string.pending_request_verify));
        mBinding.negativeButton.setText(transactionDetails.getRequest().getReject().length() > 0 ?
                transactionDetails.getRequest().getReject() : getString(R.string.pending_request_deny));

        CovrCircleTimer timer = CovrCircleTimer.newInstance(mBinding.timerText.getContext(), new TimerInfo(transactionDetails.getCreated(), transactionDetails.getValidTo()));
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        holder.mTimerWrapper.removeAllViews();
//        mBinding.timerText.addView(timer, params);
//        holder.mTimer = timer;
        Timber.d("Set Timer listener for: %s", transactionDetails.getCompany().getName());
//        mBinding.timerText.setListener(new CircleTimer.TimerAnimationListener() {
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                Timber.d("Set Timer finished! Remove item %s", transactionDetails.getCompany().getName());
//                AppAdapter.cancelTimerSound(transactionDetails);
//                SoundUtils.playTimeoutSound();
////                queueRemoveItem(transactionDetails, PendingRequestsAdapter.RemovalReason.TIMED_OUT);
////                mPendingRequestClickListener.onPendingRequestTimedOut();
//            }
//        });

//        new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                mBinding.timerText.setText("seconds remaining: " + millisUntilFinished / 1000);
//                //here you can have your logic to set text to edittext
//            }
//
//            public void onFinish() {
//                mBinding.timerText.setText("done!");
//            }
//
//        }.start();

        new CountDownTimer(transactionDetails.getCreated(), 1000) { // adjust the milliseconds here

            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {

                mBinding.timerText.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                mBinding.timerText.setText("done!");
            }
        }.start();


        StandingByFragment fragment = (StandingByFragment) getFragmentManager().findFragmentByTag(StandingByFragment.class.getName());
        mBinding.negativeButton.setOnClickListener(v -> {
            if (fragment != null) {
                fragment.postTransaction(transactionDetails, false);
                onBackPressed();
            }
        });
        mBinding.positiveButton.setOnClickListener(v -> {
            if (fragment != null) {
                fragment.postTransaction(transactionDetails, true);
                onBackPressed();
            }
        });
    }
}
