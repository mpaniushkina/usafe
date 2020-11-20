package com.covrsecurity.io.ui.fragment.authorized;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentPartnershipBinding;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.ui.adapter.PartnershipAdapter;
import com.covrsecurity.io.ui.component.AnimationEndListner;
import com.covrsecurity.io.ui.component.IPartnershipClickListener;
import com.covrsecurity.io.ui.fragment.BaseParentFragment;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModel;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModelFactory;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.model.Fields;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.covrsecurity.io.sdk.utils.QrCodeRequestResource.qrCodeStringValue;

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

    @Nullable
    private String mQrCode;
    private String fullName;
    private String logo;

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
        qrCodeStringValue = null;
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

        viewModel.addConnectionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();
                    logo = response.getCompany().getLogo();
                    fullName = response.getCompany().getFullName();
                    showChildFragment();
                },
                throwable -> {
                    hideProgress();
                    Timber.e("" + throwable);
                    Timber.e(throwable);
                }
        ));
        viewModel.qrCodeConnectionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();
                    mQrCode = qrCodeStringValue;
                    String decodedQrCode = null;
                    try {
                        decodedQrCode = URLDecoder.decode(mQrCode, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("utf8", "conversion", e);
                    }
                    viewModel.addConnection(decodedQrCode);
                },
                throwable -> {
                    hideProgress();
                    Timber.e("" + throwable);
                    Timber.e(throwable);
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
//        mBinding.bottomButton.addPartnership.setOnClickListener(v -> getQrCodeConnection());
//        mBinding.bottomButton.addPartnershipPlus.setOnClickListener(v -> getQrCodeConnection());
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
        //TODO delete hardcoded data
        partnershipList.add(new MerchantEntity("id", "UserName", 100500, null, StatusEntity.ACTIVE));
        partnershipList.add(new MerchantEntity("id", "UserName", 100500, null, StatusEntity.ACTIVE));
        partnershipList.add(new MerchantEntity("id", "UserName", 100500, null, StatusEntity.ACTIVE));
        partnershipList.add(new MerchantEntity("id", "UserName", 100500, null, StatusEntity.ACTIVE));
        partnershipList.add(new MerchantEntity("id", "UserName", 100500, null, StatusEntity.ACTIVE));
        partnershipList.add(new MerchantEntity("id", "UserName", 100500, null, StatusEntity.ACTIVE));
        partnershipList.add(new MerchantEntity("id", "UserName", 100500, null, StatusEntity.ACTIVE));
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
//        Animation fadeOut = AnimationUtils.loadAnimation(activity, R.anim.disappear);
//        fadeOut.setAnimationListener(new AnimationEndListner() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.up_bottom);
////                mBinding.slidingUpPanel.startAnimation(bottomUp);
////                mBinding.slidingUpPanel.setVisibility(View.GONE);
//                ActivityUtils.scheduleOnMainThread(() -> {
//                    getChildFragmentManager().popBackStack();
//                }, ConstantsUtils.HALF_SECOND);
//            }
//        });
//        mBinding.childFragmentTopShadow.startAnimation(fadeOut);
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
    }

    private void getQrCodeConnection() {
        viewModel.getQrCodeConnection(Fields.GET_QRCODE_COMPANY_USER_ID, Fields.GET_QRCODE_TRANSACTION_ID);
    }

    @Override
    public void showChildFragment() {
        Fragment fragment = ConnectionEstablishedFragment.newInstance(logo, fullName);
        showChildFragment(fragment, fragment.getArguments());
    }
    public void showChildFragment(Fragment fragment, Bundle args) {
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
        replaceChildFragment(fragment, args, false, R.animator.right_slide_in,
                R.animator.left_slide_in,
                R.animator.fade_in,
                R.animator.left_slide_in);
//        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
//        bottomUp.setAnimationListener(new AnimationEndListner() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.appear);
////                mBinding.childFragmentTopShadow.startAnimation(fadeOut);
////                mBinding.childFragmentTopShadow.setVisibility(View.VISIBLE);
//            }
//        });
//        mBinding.slidingUpPanel.startAnimation(bottomUp);
//        mBinding.slidingUpPanel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideProgress() {
        super.hideProgress();
        mBinding.rlMerchantProgress.setVisibility(View.GONE);
    }
}
