package com.covrsecurity.io.ui.fragment.authorized;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.GlideApp;
import com.covrsecurity.io.databinding.FragmentConnectionsServicesDetailsBinding;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.ui.fragment.FromMenuFragment;
import com.covrsecurity.io.ui.viewmodel.partnershipdetaills.PartnershipDetailsViewModel;
import com.covrsecurity.io.ui.viewmodel.partnershipdetaills.PartnershipDetailsViewModelFactory;
import com.covrsecurity.io.utils.DateTimeUtils;

import org.joda.time.DateTime;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by alex on 2.5.16.
 */
public class PartnershipDetailsFragment extends FromMenuFragment<FragmentConnectionsServicesDetailsBinding, PartnershipDetailsViewModel> {

    private static final String KEY_PARTNERSHIP_ITEM = "key_partnership_item";

    public static Fragment newInstance(MerchantEntity partnership) {
        PartnershipDetailsFragment fragment = new PartnershipDetailsFragment();
        Bundle data = new Bundle();
        data.putSerializable(KEY_PARTNERSHIP_ITEM, partnership);
        fragment.setArguments(data);
        return fragment;
    }

    @Inject
    PartnershipDetailsViewModelFactory vmFactory;

    private MerchantEntity mPartnership;

    @Override
    protected int getTitleId() {
        return R.string.services_details_title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_connections_services_details;
    }

    @NonNull
    @Override
    protected Class<PartnershipDetailsViewModel> getViewModelClass() {
        return PartnershipDetailsViewModel.class;
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
//        viewModel.markConnectionAsViewedLiveData.observe(this, new BaseObserver<>(
//                null,
//                response -> {
//                    Timber.d("merchant successfully marked as viewed");
//                    AppAdapter.markPartnershipAsViewed(mPartnership.getId());
//                },
//                throwable -> {
//                    Timber.d("error marking merchant as viewed");
//                    if (throwable != null && ConnectException.class.equals(throwable.getClass())) {
//                        showNoInternetDialog();
//                    }
//                }
//        ));
//        if (!mPartnership.getCompany().isViewed()) {
//            viewModel.markConnectionAsViewed(mPartnership.getCompany().getCompanyId());
//        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        getPartnership();
        if (mPartnership != null) {
            GlideApp.with(AppAdapter.context())
                    .load(mPartnership.getCompany().getLogo())
                    .centerCrop()
                    .error(R.drawable.iamlogo2x)
                    .into(mBinding.connectionLogo);
            mBinding.companyName.setText(mPartnership.getCompany().getName());
            mBinding.companyWebSite.setText(mPartnership.getCompany().getWebsiteUrl());
            mBinding.companyWebSite.setOnClickListener(view -> startBrowser(mPartnership.getCompany().getWebsiteUrl()));
            mBinding.companyDate.setText(mPartnership.getCreatedDate() == 0
                    ? getString(R.string.history_details_not_available) : DateTimeUtils.getFormattedTime(new DateTime(mPartnership.getCreatedDate())));
//            boolean isActive = mPartnership.getCompany().isActive();
//            mBinding.setPartnerStatus(isActive ? getString(R.string.company_status_active) :
//                    getString(R.string.company_status_inactive));
        }
    }

    private void startBrowser(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Timber.e(e, "Unable to start activity");
        }
    }

    private void getPartnership() {
        if (getArguments() != null) {
            mPartnership = (MerchantEntity) getArguments().getSerializable(KEY_PARTNERSHIP_ITEM);
        }
    }
}
