package com.covrsecurity.io.ui.fragment.authorized;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentPartnershipBinding;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.ui.adapter.PartnershipAdapter;
import com.covrsecurity.io.ui.component.IPartnershipClickListener;
import com.covrsecurity.io.ui.fragment.BaseParentFragment;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModel;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModelFactory;
import com.covrsecurity.io.utils.ConstantsUtils;

import java.net.ConnectException;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by alex on 2.5.16.
 */
public class PartnershipFragment extends BaseParentFragment<FragmentPartnershipBinding, PartnershipViewModel> implements IPartnershipClickListener {

    public static Fragment newInstance() {
        return new PartnershipFragment();
    }

    @Inject
    PartnershipViewModelFactory vmFactory;

    private PartnershipAdapter mPartnershipAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_partnership;
    }

    @Override
    protected int getTitleId() {
        return R.string.connected_services_title;
    }

    @NonNull
    @Override
    protected Class<PartnershipViewModel> getViewModelClass() {
        return PartnershipViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.connectionsLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();

                    if (response.getPageNumber() == 0) {
                        AppAdapter.cleanPartnerships();
                    }
                    if (response.getMerchants() != null && !response.getMerchants().isEmpty()) {
                        AppAdapter.setPartnerships(response.getMerchants());
                        updateListContent();
                    }
                    showEmpty(mBinding.partnershipRecyclerView.getAdapter() == null
                            || mBinding.partnershipRecyclerView.getAdapter().getItemCount() < 1);
                    if (response.isHasNext()) {
                        getAllMerchant(response.getPageNumber() + 1);
                    }
                },
                throwable -> {

                    hideProgress();

                    Timber.e("" + throwable);
                    Timber.e(throwable);

                    if (throwable != null && ConnectException.class.equals(throwable.getClass())) {
                        showNoInternetDialog();
                    }
                    showEmpty(mBinding.partnershipRecyclerView.getAdapter() == null
                            || mBinding.partnershipRecyclerView.getAdapter().getItemCount() < 1);
                }
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllMerchant(ConstantsUtils.DEFAULT_PAGE_NUMBER);
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.rlMerchantProgress.setVisibility(View.VISIBLE);
        mBinding.partnershipRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setAdapter(AppAdapter.getPartnerships());
    }

    private void updateListContent() {
        Timber.d("update partnerships List Content");
        if (mPartnershipAdapter != null) {
            mPartnershipAdapter.setData(AppAdapter.getPartnerships());
        }
    }

    private void getAllMerchant(final int pageNumber) {
        viewModel.getConnections(pageNumber);
    }

    private void setAdapter(final List<MerchantEntity> partnershipList) {
        if (mPartnershipAdapter == null) {
            mPartnershipAdapter = new PartnershipAdapter(partnershipList, this);
        } else {
            mPartnershipAdapter.setData(partnershipList);
        }
        if (mBinding.partnershipRecyclerView.getAdapter() == null) {
            mBinding.partnershipRecyclerView.setAdapter(mPartnershipAdapter);
        }
    }

    @Override
    public void onPartnershipClicked(MerchantEntity partnership) {
        Timber.d("partnership clicked. id = " + partnership.getId());
        Fragment fragment = PartnershipDetailsFragment.newInstance(partnership);
        replaceFragment(fragment, fragment.getArguments(), true);
    }

    private void showEmpty(boolean show) {
        mBinding.partnershipRecyclerView.setVisibility(show ? View.VISIBLE : View.VISIBLE);
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    public void closeChildFragment() {
        getAllMerchant(ConstantsUtils.DEFAULT_PAGE_NUMBER);
        updateListContent();
        showEmpty(mBinding.partnershipRecyclerView.getAdapter() == null
                || mBinding.partnershipRecyclerView.getAdapter().getItemCount() < 1);
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
    }

    @Override
    public void showChildFragment() {
    }

    @Override
    protected void hideProgress() {
        super.hideProgress();
        mBinding.rlMerchantProgress.setVisibility(View.GONE);
    }
}
