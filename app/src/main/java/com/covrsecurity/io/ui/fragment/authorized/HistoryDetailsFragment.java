package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.GlideApp;
import com.covrsecurity.io.databinding.FragmentHistoryDetailsBinding;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.ui.fragment.FromMenuFragment;
import com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.historydetails.HistoryDetailsViewModel;
import com.covrsecurity.io.ui.viewmodel.historydetails.HistoryDetailsViewModelFactory;
import com.covrsecurity.io.utils.DateTimeUtils;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.EmailUtils;
import com.covrsecurity.io.utils.StatusUtils;

import org.joda.time.DateTime;

import java.net.ConnectException;

import javax.inject.Inject;

import static com.covrsecurity.io.domain.entity.StatusEntity.ACCEPTED;

public class HistoryDetailsFragment extends FromMenuFragment<FragmentHistoryDetailsBinding, HistoryDetailsViewModel> {

    private static final String KEY_TRANSACTION_ID = "KEY_TRANSACTION_ID";
    private static final String KEY_COMPANY_ID = "KEY_COMPANY_ID";

    public static Fragment newInstance(String transactionId, String companyId) {
        Fragment fragment = new HistoryDetailsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TRANSACTION_ID, transactionId);
        args.putString(KEY_COMPANY_ID, companyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    HistoryDetailsViewModelFactory vmFactory;

    private String mTransactionId;
    private String mCompanyId;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history_details;
    }

    @Override
    protected int getTitleId() {
        return R.string.history_details_title;
    }

    @NonNull
    @Override
    protected Class<HistoryDetailsViewModel> getViewModelClass() {
        return HistoryDetailsViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected boolean showDrawerOnBack() {
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getTransactionDetailsLiveData.observe(this, new BaseObserver<>(
                () -> showProgressLayout(true),
                response -> {
                    showProgressLayout(false);
                    setUpViews(response);
                },
                throwable -> {
                    showProgressLayout(false);
                    if (throwable != null && ConnectException.class.equals(throwable.getClass())) {
                        showNoInternetDialog();
                    } else if (throwable != null) {
                        DialogUtils.showOkDialog(getActivity(), throwable.getMessage(), false, (dialog, which) -> onBackPressed());
                    }
                }
        ));
        viewModel.getTransactionDetails(mTransactionId, mCompanyId);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (getArguments() != null) {
            mTransactionId = getArguments().getString(KEY_TRANSACTION_ID);
            mCompanyId = getArguments().getString(KEY_COMPANY_ID);
        }
    }

    private void setUpViews(TransactionEntity pendingTransaction) {
        if (pendingTransaction != null && getActivity() != null) {

//            GlideApp.with(this)
//                    .load(pendingTransaction.getCompany().getLogo())
//                    .centerCrop()
//                    .error(R.drawable.iamlogo2x);
//                    .into(mBinding.ivHistoryDetailsLogo);
            mBinding.setHistoryFullName(pendingTransaction.getCompany().getName());
            mBinding.setHistoryIncomingRequest(AppAdapter.resources().getString(
                    R.string.history_details_not_available));
            mBinding.setHistoryVerifiedFrom(TextUtils.isEmpty(pendingTransaction.getVerifiedByIp())
                    ? AppAdapter.resources().getString(R.string.history_details_not_available)
                    : pendingTransaction.getVerifiedByIp());
            final String status = StatusUtils.getStatusText(getActivity(), pendingTransaction);
            mBinding.setHistoryStatus(status);
            mBinding.setDescription(StatusUtils.getHistoryText(getActivity(), pendingTransaction));
            String time = DateTimeUtils.getFormattedTime(new DateTime(pendingTransaction.getCreated()));
            mBinding.setHistoryTime(time);
            mBinding.setHistoryVerificationType(pendingTransaction.getRequest().getTitle());
            switch (pendingTransaction.getStatus()) {
                case ACCEPTED:
                    mBinding.statusApproved.setVisibility(View.VISIBLE);
                    break;
                case REJECTED:
                    mBinding.statusDeclined.setVisibility(View.VISIBLE);
                    break;
                case EXPIRED:
                    mBinding.statusExpired.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void showProgressLayout(boolean show) {
        mBinding.progressLayout.container.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
