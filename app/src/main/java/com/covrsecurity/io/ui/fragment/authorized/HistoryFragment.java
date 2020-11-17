package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentHistoryBinding;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.adapter.HistoryAdapter;
import com.covrsecurity.io.ui.component.RecyclerViewPaginationListener;
import com.covrsecurity.io.ui.fragment.FromMenuFragment;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.history.HistoryViewModel;
import com.covrsecurity.io.ui.viewmodel.history.HistoryViewModelFactory;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConnectivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by elena on 5/2/16.
 */
public class HistoryFragment extends FromMenuFragment<FragmentHistoryBinding, HistoryViewModel> implements
        HistoryAdapter.IHistoryClickListener {

    private static final int VISIBLE_THRESHOLD = 3;

    public static Fragment newInstance() {
        return new HistoryFragment();
    }

    @Inject
    HistoryViewModelFactory vmFactory;

    private RecyclerViewPaginationListener mRecyclerViewPaginationListener;
    private HistoryAdapter mAdapter;
    private HistoryAdapter mExpiredAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history;
    }

    @Override
    protected int getTitleId() {
        return R.string.history;
    }

    @NonNull
    @Override
    protected Class<HistoryViewModel> getViewModelClass() {
        return HistoryViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    public void onHistoryClick(TransactionEntity pendingTransaction) {
        mAdapter.setViewed(pendingTransaction);
        mExpiredAdapter.setViewed(pendingTransaction);
        Fragment fragment = HistoryDetailsFragment.newInstance(pendingTransaction.getId(), pendingTransaction.getCompany().getCompanyId());
        replaceFragment(fragment, fragment.getArguments(), true);
    }

    @Override
    public void onAllItemsSelected(boolean allSelected) {
    }

    @Override
    public void onHistoryItemsClick(TransactionEntity pendingTransaction) {
        mAdapter.setViewed(pendingTransaction);
//        Fragment fragment = HistoryDetailsFragment.newInstance(pendingTransaction.getId(), pendingTransaction.getCompany().getCompanyId());
        Fragment fragment = HistoryDetailsFragment.newInstance("id", "My Company");
        replaceFragment(fragment, fragment.getArguments(), true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.historyLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();

                    List<TransactionEntity> pendingTransactions = response.getTransactions();

                    if (response.getPageNumber() == ConstantsUtils.DEFAULT_PAGE_NUMBER) {
                        AppAdapter.setHistoryCache(pendingTransactions);
                        setScrollListener(response.isHasNext());
                        setAdapter(pendingTransactions);
                    } else {
                        setAdapter(pendingTransactions);
                        mRecyclerViewPaginationListener.setHasNext(response.isHasNext());
                    }
                },
                throwable -> {

                    hideProgress();
                    Timber.e(throwable);
                    showErrToast(throwable);

                    showEmpty(mBinding.partnershipRecyclerView.getAdapter() == null
                            || mBinding.partnershipRecyclerView.getAdapter().getItemCount() < 1);
//                    showEmptyExpired(mBinding.partnershipExpiredRecyclerView.getAdapter() == null
//                            || mBinding.partnershipExpiredRecyclerView.getAdapter().getItemCount() < 1);
                }
        ));
        viewModel.markHistoryAsViewedLiveData.observe(this, new BaseObserver<>(
                null,
                isSucceeded -> Timber.d("Mark history viewed result: %b", isSucceeded),
                throwable -> {

                    Timber.d("error marking history viewed");

                    if (throwable != null && (ConnectException.class.equals(throwable.getClass()))) {
                        showNoInternetDialog();
                    }
                }
        ));
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setAllClickListener(v -> {
            selectAll();
            showEmptyExpired(true);
            showEmpty(mBinding.partnershipRecyclerView.getAdapter() == null
                    || mBinding.partnershipRecyclerView.getAdapter().getItemCount() < 1);
        });
//        mBinding.setTimedOutClickListener(v -> {
//            selectTimedOut();
//            showEmpty(true);
//            showEmptyExpired(mBinding.partnershipExpiredRecyclerView.getAdapter() == null
//                    || mBinding.partnershipExpiredRecyclerView.getAdapter().getItemCount() < 1);
//        });
        //TODO it's commented via UI isn't completed
//        Handler handler = new Handler(Looper.getMainLooper());
//        new Thread(() -> {
//            if (ConnectivityUtils.hasNetworkConnection()) {
//                handler.post(this::startRequests);
//            } else {
//                ActivityUtils.runOnMainThread(this::onBackPressed);
//            }
//        }).start();

        //TODO delete after request is implemented
        List<TransactionEntity> itemsList = new ArrayList<>();
        TransactionEntity entity = new TransactionEntity("id", null, "companyClientId", "templateId", null, 100600, 100500, null, "createdByIp", "verifiedByIp", 100500, 100500, "acceptHistoryMessage", "rejectHistoryMessage", "expiredHistoryMessage", "historyMessage", null, false, null, null);
        itemsList.add(entity);
        itemsList.add(entity);
        itemsList.add(entity);
        itemsList.add(entity);
        itemsList.add(entity);
        setAdapter(itemsList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((AuthorizedActivity) getActivity()).isHistoryAllSelected()) {
            selectAll();
        } else {
            selectTimedOut();
        }
        viewModel.markHistoryAsViewed();
    }

    @Override
    protected void showProgress() {
        mBinding.progressLayout.container.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideProgress() {
        super.hideProgress();
        mBinding.progressLayout.container.setVisibility(View.GONE);
    }

    @Override
    protected void onLeftToolbarButtonClicked() {
        AppAdapter.settings().setLastTimeHistoryViewed(System.currentTimeMillis());
        getActivity().onBackPressed();
    }

    @Override
    protected void onBackPressed() {
        super.onBackPressed();
        AppAdapter.settings().setLastTimeHistoryViewed(System.currentTimeMillis());
    }

    private void startRequests() {
        if (mAdapter != null) {
            setAdapter(mAdapter.getAll());
        } else {
            viewModel.getHistory(ConstantsUtils.DEFAULT_PAGE_NUMBER);
        }
        ActivityUtils.scheduleOnMainThread(() -> viewModel.markHistoryAsViewed(), ConstantsUtils.HALF_SECOND);
    }

    private void setAdapter(final List<TransactionEntity> pendingTransactions) {
        if (mBinding.partnershipRecyclerView.getAdapter() == null) {
            mAdapter = new HistoryAdapter(pendingTransactions, HistoryFragment.this);
            mBinding.partnershipRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.addAll(pendingTransactions);
        }

//        if (mBinding.partnershipExpiredRecyclerView.getAdapter() == null) {
//            mExpiredAdapter = new HistoryAdapter(getExpired(pendingTransactions), HistoryFragment.this);
//            mBinding.partnershipExpiredRecyclerView.setAdapter(mExpiredAdapter);
//        } else {
//            mExpiredAdapter.addAll(getExpired(pendingTransactions));
//        }
        if (((AuthorizedActivity) getActivity()).isHistoryAllSelected()) {
            showEmpty((pendingTransactions == null || pendingTransactions.isEmpty())
                    && (mBinding.partnershipRecyclerView.getAdapter() == null
                    || mBinding.partnershipRecyclerView.getAdapter().getItemCount() < 1));
        } else {
//            showEmptyExpired((pendingTransactions == null || pendingTransactions.isEmpty())
//                    && mBinding.partnershipExpiredRecyclerView.getAdapter() == null
//                    || mBinding.partnershipExpiredRecyclerView.getAdapter().getItemCount() < 1);
        }
    }

    private void selectAll() {
//        ((AuthorizedActivity) getActivity()).setHistoryAllSelected(true);
//        mBinding.toolbarAll.setChecked(true);
//        mBinding.toolbarTimedOut.setChecked(false);
    }

    private void selectTimedOut() {
//        ((AuthorizedActivity) getActivity()).setHistoryAllSelected(false);
//        mBinding.toolbarAll.setChecked(false);
//        mBinding.toolbarTimedOut.setChecked(true);
    }

    private void setScrollListener(boolean hasNext) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mBinding.partnershipRecyclerView.getLayoutManager();
        if (hasNext && layoutManager != null && mBinding.partnershipRecyclerView != null && mRecyclerViewPaginationListener == null) {
            mRecyclerViewPaginationListener = new RecyclerViewPaginationListener(layoutManager) {
                @Override
                public void onLoadMore(int page) {
                    Timber.d("load more");
                    viewModel.getHistory(page);
                }
            };
            mRecyclerViewPaginationListener.setVisibleThreshold(VISIBLE_THRESHOLD);
            mBinding.partnershipRecyclerView.addOnScrollListener(mRecyclerViewPaginationListener);
        }
    }

    private void showEmpty(boolean show) {
        mBinding.partnershipRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
//        mBinding.noHistory.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyExpired(boolean show) {
//        mBinding.partnershipExpiredRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
//        mBinding.noHistory.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private List<TransactionEntity> getExpired(List<TransactionEntity> requestList) {
        List<TransactionEntity> result = new ArrayList<>();
        if (requestList != null && !requestList.isEmpty()) {
            for (TransactionEntity pendingTransaction : requestList) {
                if (pendingTransaction.getStatus() == StatusEntity.EXPIRED) {
                    result.add(pendingTransaction);
                }
            }
        }
        return result;
    }
}
