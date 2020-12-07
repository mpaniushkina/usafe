package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentPendingDetailsBinding;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.ui.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class PendingDetailsFragment extends BaseFragment<FragmentPendingDetailsBinding> {

    private static final String PENDING_TRANSACTION = "PENDING_TRANSACTION";

    private TransactionEntity transactionDetails;

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
