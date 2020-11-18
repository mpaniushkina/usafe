package com.covrsecurity.io.ui.fragment.authorized;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentStandingByBinding;
import com.covrsecurity.io.domain.entity.BiometricEntity;
import com.covrsecurity.io.domain.entity.BiometricTypeEntity;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.event.GetMessagesEvent;
import com.covrsecurity.io.event.HistoryBadgeEvent;
import com.covrsecurity.io.event.NotificationHubConnectedEvent;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.model.deeplink.AuthorizationDeeplink;
import com.covrsecurity.io.model.error.BioMetricDataProvideCancel;
import com.covrsecurity.io.sdk.exception.BiometricVerificationAttemptsExhaustedException;
import com.covrsecurity.io.sdk.utils.ThreadUtils;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.adapter.PendingRequestsAdapter;
import com.covrsecurity.io.ui.component.AnimationEndListner;
import com.covrsecurity.io.ui.component.SlideLeftItemAnimator;
import com.covrsecurity.io.ui.component.SmoothLinearLayoutManager;
import com.covrsecurity.io.ui.dialog.ScanQrCodeDialog;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.ui.fragment.BaseViewModelFragment;
import com.covrsecurity.io.ui.fragment.authorized.vault.CovrVaultAboutQuickBar;
import com.covrsecurity.io.ui.fragment.unauthorized.CreatePersonalCodeFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.PrivacyPolicyFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ScanFaceBiometricsFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ScanQrCodeFragment;
import com.covrsecurity.io.ui.interfaces.IChildFragmentListener;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.biometricsshared.BiometricsSharedViewModel;
import com.covrsecurity.io.ui.viewmodel.biometricsshared.BiometricsSharedViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModel;
import com.covrsecurity.io.ui.viewmodel.partnership.PartnershipViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.standingby.PostTransactionPending;
import com.covrsecurity.io.ui.viewmodel.standingby.StandingByViewModel;
import com.covrsecurity.io.ui.viewmodel.standingby.StandingByViewModelFabric;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.LogUtil;
import com.covrsecurity.io.model.Fields;
import com.instacart.library.truetime.TrueTime;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.covrsecurity.io.sdk.utils.QrCodeRequestResource.qrCodeStringValue;

public class StandingByFragment extends BaseViewModelFragment<FragmentStandingByBinding, StandingByViewModel>
        implements PendingRequestsAdapter.IPendingRequestListListener,
        PendingRequestsAdapter.IPendingRequestsCountChangeListener,
        IChildFragmentListener {

    private static final int SHOW_BUTTON_TIMEOUT = 3000;

    @Inject
    StandingByViewModelFabric vmFactory;
    @Inject
    BiometricsSharedViewModelFactory sharedVmFactory;

    private PendingRequestsAdapter mPartnershipAdapter;
    private Animation hideButtonAnimation;
    private Animation showButtonAnimation;
    private long lastListInteractionTime = 0;

    private BiometricsSharedViewModel sharedViewModel;
    @Inject
    PartnershipViewModelFactory addConnectionVmFactory;
    private PartnershipViewModel addConnectionViewModel;
    private Disposable mDisposable;

    public static Fragment newInstance() {
        return new StandingByFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_standing_by;
    }

    @NonNull
    @Override
    protected Class<StandingByViewModel> getViewModelClass() {
        return StandingByViewModel.class;
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
        sharedViewModel = ViewModelProviders.of(getActivity(), sharedVmFactory).get(BiometricsSharedViewModel.class);
        addConnectionViewModel = ViewModelProviders.of(getActivity(), addConnectionVmFactory).get(PartnershipViewModel.class);
        sharedViewModel.sharedLiveData.observe(this, new BaseObserver<>(
                null,
                response -> {
                    closeChildFragment();
                    viewModel.postTransaction(response.getTransaction(), response.isAccept(), response.getBiometricsData());
                },
                throwable -> {
                    if (throwable instanceof BioMetricDataProvideCancel) {
                        BioMetricDataProvideCancel dataProvideCancel = (BioMetricDataProvideCancel) throwable;
                        String transactionId = dataProvideCancel.getTransactionId();
                        mPartnershipAdapter.setItemButtonDeactivated(transactionId);
                    } else {
                        Timber.w(throwable);
                    }
                }
        ));
        viewModel.connectionsLiveData.observe(this, new BaseObserver<>(
                null,
                response -> {
                    Timber.d("Merchants successfully received");
                    if (response.getPageNumber() == 0) {
                        AppAdapter.cleanPartnerships();
                    }
                    List<MerchantEntity> merchants = response.getMerchants();
                    if (merchants != null) {
                        AppAdapter.setPartnerships(merchants);
                    }
                    if (response.isHasNext()) {
                        viewModel.getConnections(response.getPageNumber() + 1);
                    }
                },
                throwable -> {
                    Timber.d("Error getting merchants");
                    showServerErrorDialog();
                    if (throwable != null && ConnectException.class.equals(throwable.getClass())) {
                        Timber.e("Server error during GetMerchantsRequest : %s", throwable.getMessage());
                    }
                }
        ));
        viewModel.transactionsLiveData.observe(this, new BaseObserver<>(
                null,
                response -> {

                    if (response.getPageNumber() == 0) {
                        AppAdapter.cleanConsumerRequests();
                    }

                    AppAdapter.addConsumerRequests(response.getTransactions());

                    if (response.isHasNext()) {
                        viewModel.getTransactions(response.getPageNumber() + 1);
                    } else {
                        List<TransactionEntity> pendingTransactions = AppAdapter.getPendingConsumerRequests();
                        updateListContent(pendingTransactions);
                        updatePendingRequestsExpireDate(pendingTransactions);
                        openFirstRequest();
                    }
                },
                throwable -> {
                    Timber.e(throwable);
                    showErrToast(throwable);
                }
        ));
        viewModel.unreadHistoryCountLiveData.observe(this, new BaseObserver<>(
                null,
                response -> {
                    AppAdapter.bus().postSticky(new HistoryBadgeEvent(response.getCount()));
//                    setMenuBadgeCount(response.getCount());
                },
                throwable -> {
                    Timber.d("Error running getHistoryBadgeCount() request");
                    showServerErrorDialog();
                    if (throwable != null && ConnectException.class.equals(throwable.getClass())) {
                        Timber.e("Server error during GetHistoryBadgeCountRequest : %s", throwable.getMessage());
                    }
                }
        ));
        viewModel.requestFinishedLiveData.observe(this, new BaseObserver<>(
                null,
                response -> hideProgress(),
                null
        ));
        viewModel.postTransactionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();

                    if (StatusEntity.ACCEPTED == response.getStatus()) {
                        onRemoveRequest(
                                response,
                                PendingRequestsAdapter.RemovalReason.APPROVED,
                                Analytics.EVENT_REQUEST_VERIFY
                        );
                    } else if (StatusEntity.REJECTED == response.getStatus()) {
                        onRemoveRequest(
                                response,
                                PendingRequestsAdapter.RemovalReason.REJECTED,
                                Analytics.EVENT_REQUEST_DENY
                        );
                    }
                },
                throwable -> {

                    hideProgress();

                    Timber.w(throwable);

                    mPartnershipAdapter.resetActiveItem();

                    if (throwable instanceof BiometricVerificationAttemptsExhaustedException) {
                        loadAllData();
                    } else if (throwable instanceof ConnectException) {
                        showNoInternetDialog();
                    } else {
                        showErrToast(throwable);
                    }
                }
        ));
        addConnectionViewModel.addConnectionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();
                    Fragment fragment = ConnectionEstablishedFragment.newInstance(
                            response.getCompany().getLogo(),
                            response.getCompany().getFullName()
                    );
                    showChildFragment(fragment, fragment.getArguments());
                },
                throwable -> {
                    hideProgress();
                    Timber.e("" + throwable);
                    Timber.e(throwable);
                }
        ));
        addConnectionViewModel.qrCodeConnectionLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {

                    hideProgress();
                    String mQrCode;
                    mQrCode = qrCodeStringValue;
                    String decodedQrCode = qrCodeStringValue;
                    try {
                        decodedQrCode = URLDecoder.decode(mQrCode, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("utf8", "conversion", e);
                    }
                    addConnectionViewModel.addConnection(decodedQrCode);
                },
                throwable -> {
                    hideProgress();
                    Timber.e("" + throwable);
                    Timber.e(throwable);
                }
        ));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.SCAN_QR_REQUEST_CODE && Activity.RESULT_OK == resultCode) {
            final String qrCode = ScanQrCodeDialog.parseQrCodeResult(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter(AppAdapter.getPendingConsumerRequests());
//        mBinding.slidingUpPanel.setVisibility(View.GONE);
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
        ActivityUtils.setLastFragmentName(this.getClass().getName());
        ActivityUtils.scheduleOnMainThread(() -> {
            openFirstRequest();
        }, ConstantsUtils.THREE_HUNDRED_MILLISECONDS);
        if (((AuthorizedActivity) getActivity()).isShouldAddConnectionShown()) {
            getQrCodeConnection();
        }
        viewModel.checkLocked();
    }

    @Override
    public void onPause() {
        stopUpdates();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        AppAdapter.bus().register(this);
    }

    @Override
    public void onStop() {
        AppAdapter.bus().unregister(this);
        super.onStop();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void stopUpdates() {
        // TODO: 05.06.2018
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        Animation progress = AnimationUtils.loadAnimation(getActivity(), R.anim.progress_anim);
//        mBinding.progress.startAnimation(progress);
        mBinding.pendindRequestsRecyclerView.setLayoutManager(new SmoothLinearLayoutManager(getActivity()));
        mBinding.pendindRequestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Timber.d("OnScrollStateChanged: %d", newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && mPartnershipAdapter.getItemCount() > 0) {
                    onListInteractionOccured();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mBinding.pendindRequestsRecyclerView.setItemAnimator(new SlideLeftItemAnimator());
//        mBinding.bottomButton.addPartnership.setOnClickListener(v -> getQrCodeConnection());
//        mBinding.bottomButton.addPartnershipPlus.setOnClickListener(v -> getQrCodeConnection());
//        mBinding.menu.setOnClickListener(v -> ((AuthorizedActivity) getActivity()).openDrawer());
        hideButtonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_button_hide);
        showButtonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_button_show);
        mBinding.scanQr.setOnClickListener(view -> {
            if (!isCameraPermissionsGranted()) {
                Fragment f = ScanQrCodeFragment.newInstance();
                replaceFragment(f, f.getArguments(), true, FragmentAnimationSet.FADE_IN);
            } else {
                scanQrCode();
            }
        });
        mBinding.myAccount.setOnClickListener(view -> {
            Fragment f = MyAccountFragment.newInstance();
            replaceFragment(f, null, true);
        });
        mBinding.settings.setOnClickListener(view -> {
            Fragment f = SettingsFragment.newInstance();
            replaceFragment(f, null, true);
        });
    }

    private void scanQrCode() {
        final ScanQrCodeDialog scanQrCodeDialog = new ScanQrCodeDialog();
        scanQrCodeDialog.show(getFragmentManager(), ScanQrCodeDialog.QR_CODE_TAG);
        scanQrCodeDialog.setTargetFragment(this, ConstantsUtils.SCAN_QR_REQUEST_CODE);
    }

    private void getQrCodeConnection() {
        addConnectionViewModel.getQrCodeConnection(Fields.GET_QRCODE_COMPANY_USER_ID, Fields.GET_QRCODE_TRANSACTION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            final PostTransactionPending transaction = viewModel.getPostedTransaction();
            if (transaction != null) {
                final ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance(
                        transaction.getPendingTransaction(),
                        transaction.isAccept()
                );
                ThreadUtils.scheduleOnMainThread(() ->
                        showChildFragment(fragment, fragment.getArguments())
                );
            }
        } else {
            showToast(R.string.camera_permission_declined);
        }
    }

    private boolean isCameraPermissionsGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(getActivity(), ConstantsUtils.CAMERA_PERMISSION, ConstantsUtils.CAMERA_REQUEST_CODE);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0x0) {
            Animator animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!enter) {
                        closeChildFragment();
                        for (int i = 0; i < getChildFragmentManager().getBackStackEntryCount(); i++) {
                            getChildFragmentManager().popBackStack();
                        }
                    }
                }
            });
            return animator;
        }
        return null;
    }

    private void showFavoriteReadMore() {
        replaceFragment(CovrVaultAboutQuickBar.newInstance(), null, true, FragmentAnimationSet.SLIDE_LEFT);
    }

    private void updateListContent(List<TransactionEntity> pendingTransactions) {
        mPartnershipAdapter.setData(pendingTransactions);
    }

    private void onListInteractionOccured() {
        lastListInteractionTime = TrueTime.now().getTime();
    }

    private void hideBottomButton() {
        //TODO: temporarily disabled
//        ActivityUtils.scheduleOnMainThread(() -> {
//            Timber.d("Hide button animation");
//            mBinding.bottomButton.setAnimation(hideButtonAnimation);
//            mBinding.bottomButton.setVisibility(View.INVISIBLE);
//            mBinding.bottomButton.animate();
//            ActivityUtils.scheduleOnMainThread(() -> {
//                showBottomButton();
//            }, SHOW_BUTTON_TIMEOUT);
//        });
    }

    private void showBottomButton() {
        //TODO: temporarily disabled
//        if (mBinding.bottomButton.getVisibility() != View.VISIBLE) {
//            if ((lastListInteractionTime + SHOW_BUTTON_TIMEOUT) <= new Date().getTime()) {
//                mBinding.bottomButton.setAnimation(showButtonAnimation);
//                mBinding.bottomButton.setVisibility(View.VISIBLE);
//                mBinding.bottomButton.animate();
//            } else {
//                ActivityUtils.scheduleOnMainThread(() -> {
//                    showBottomButton();
//                }, SHOW_BUTTON_TIMEOUT);
//            }
//        }
    }

    private void setAdapter(List<TransactionEntity> pendingTransactionsList) {
        if (mPartnershipAdapter == null) {
            mPartnershipAdapter = new PendingRequestsAdapter(pendingTransactionsList, this, this, this);
        } else {
            mPartnershipAdapter.setData(pendingTransactionsList);
        }
//        mPartnershipAdapter.setEmptyView(mBinding.emptyView);
        mPartnershipAdapter.setLogoView(mBinding.logo);
        if (mBinding.pendindRequestsRecyclerView.getAdapter() == null) {
            mBinding.pendindRequestsRecyclerView.setAdapter(mPartnershipAdapter);
        }
    }

    @Override
    public void onPendingRequestClicked(TransactionEntity pendingTransaction) {
        onListInteractionOccured();
    }

    @Override
    public void onRejectRequestClicked(TransactionEntity pendingTransaction) {
        postTransaction(pendingTransaction, false);
    }

    @Override
    public void onAcceptRequestClicked(TransactionEntity pendingTransaction) {
        postTransaction(pendingTransaction, true);
    }

    //smoothly scroll to just opened item if its not fully visible
    @Override
    public void onPendingRequestOpened(int position) {
        ActivityUtils.scheduleOnMainThread(() -> {
            LinearLayoutManager llm = (LinearLayoutManager) mBinding.pendindRequestsRecyclerView.getLayoutManager();
            if (position > llm.findLastCompletelyVisibleItemPosition()) {
                mBinding.pendindRequestsRecyclerView.smoothScrollToPosition(position);
            }
        }, ConstantsUtils.FOUR_HUNDRED_MILLISECONDS);
    }

    @Override
    public void onPendingRequestTimedOut() {
        if (isVisible()) {
//            final int menuBadgeCount = incrementMenuBadgeCount();
//            AppAdapter.bus().postSticky(new HistoryBadgeEvent(menuBadgeCount));
        }
    }

    @Override
    public void showChildFragment() {
        throw new UnsupportedOperationException();
    }

    public void showChildFragment(Fragment fragment, Bundle args) {
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
        replaceChildFragment(fragment, args, true, 0, 0, 0, 0);
        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
//        bottomUp.setAnimationListener(new AnimationEndListner() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.appear);
//                mBinding.childFragmentTopShadow.startAnimation(fadeOut);
//                mBinding.childFragmentTopShadow.setVisibility(View.VISIBLE);
//            }
//        });
//        mBinding.slidingUpPanel.startAnimation(bottomUp);
//        mBinding.slidingUpPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeChildFragment() {
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);
        fadeOut.setAnimationListener(new AnimationEndListner() {
            @Override
            public void onAnimationEnd(Animation animation) {
                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.up_bottom);
//                mBinding.slidingUpPanel.startAnimation(bottomUp);
//                mBinding.slidingUpPanel.setVisibility(View.GONE);
//                ActivityUtils.scheduleOnMainThread(() -> {
//                    FragmentManager childFragmentManager = getChildFragmentManager();
//                    for (int i = 0; i < childFragmentManager.getBackStackEntryCount(); i++) {
//                        final BaseFragment baseFragment = (BaseFragment) childFragmentManager
//                                .findFragmentById(getFragmentContainerId());
//                        if (baseFragment != null) {
//                            baseFragment.onKeyboardBackPressed();
//                        }
//                        childFragmentManager.popBackStack();
//                    }
//                    childFragmentManager.executePendingTransactions();
//                }, ConstantsUtils.HALF_SECOND);
            }
        });
//        mBinding.childFragmentTopShadow.startAnimation(fadeOut);
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
        ActivityUtils.setLastFragmentName(this.getClass().getName());
    }

    public void closeSlidingUpPanel() {
//        mBinding.slidingUpPanel.setVisibility(View.GONE);
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
    }

//    protected int getFragmentContainerId() {
//        return R.id.child_fragment_container;
//    }

    @Override
    public void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                     FragmentAnimationSet animations) {
        replaceChildFragment(fragment, bundle, addToBackStack,
                animations.enter, animations.exit, animations.popEnter, animations.popExit);
    }

    @Override
    public void addChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, FragmentAnimationSet animations) {
        addReplaceChildFragment(fragment, bundle, addToBackStack, animations.enter, animations.exit, animations.popEnter, animations.popExit, true);
    }

    private void addReplaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                         int enterAnimationId, int exitAnimationId, int popEnterAnimationId,
                                         int popExitAnimationId, boolean add) {
        String fragmentName = fragment.getClass().getName();
        Timber.d("Replacing fragment with %s", fragmentName);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(fragmentName);
        }
        Fragment f = Fragment.instantiate(getActivity(), fragmentName, bundle);
        if (enterAnimationId != 0 && popExitAnimationId != 0) {
            ft.setCustomAnimations(enterAnimationId, exitAnimationId, popEnterAnimationId, popExitAnimationId);
        }
        if (add) {
//            ft.add(getFragmentContainerId(), f, fragmentName);
        } else {
//            ft.replace(getFragmentContainerId(), f, fragmentName);
        }
        try {
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Timber.e(e, "Error replacing fragment");
        }
    }

    @Override
    public void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                     int enterAnimationId, int exitAnimationId, int popEnterAnimationId, int popExitAnimationId) {

        addReplaceChildFragment(fragment, bundle, addToBackStack, enterAnimationId, exitAnimationId, popEnterAnimationId, popExitAnimationId, false);
    }

    @Override
    public void onPendingRequestsCountChange(final int count) {
        Timber.d("Push count change: %d", count);
        //AppAdapter.bus().postSticky(new HistoryBadgeEvent(count));
//            mGetHistoryBadgeCountRequest.send();
    }

//    public void setMenuBadgeCount(int count) {
//        if (count > 0) {
//            mBinding.menuBadge.setVisibility(View.VISIBLE);
//            mBinding.menuBadge.setText(String.valueOf(count));
//        } else {
//            mBinding.menuBadge.setVisibility(View.INVISIBLE);
//            mBinding.menuBadge.setText("");
//        }
//    }
//
//    private int incrementMenuBadgeCount() {
//        int menuBadgeCount = getMenuBadgeCount();
//        mBinding.menuBadge.setText(String.valueOf(++menuBadgeCount));
//        mBinding.menuBadge.setVisibility(View.VISIBLE);
//        return menuBadgeCount;
//    }
//
//    private int getMenuBadgeCount() {
//        String text = mBinding.menuBadge.getText().toString();
//        int count;
//        if (TextUtils.isEmpty(text) || !TextUtils.isDigitsOnly(text)) {
//            count = 0;
//        } else {
//            count = Integer.parseInt(text);
//        }
//        return count;
//    }

    private void openFirstRequest() {
        mPartnershipAdapter.openTopItem();
    }

    private void onRemoveRequest(final TransactionEntity pendingTransaction, final PendingRequestsAdapter.RemovalReason removalReason, final String analyticsEvent) {
        hideProgress();
        Analytics.logEvent(AppAdapter.context(), analyticsEvent);
        ActivityUtils.scheduleOnMainThread(() -> {
            //SoundUtils.playResponseSound();
            mPartnershipAdapter.removeItemById(pendingTransaction.getId(), removalReason);
            //if it be correct to do this in fragment but not in adapter
            mBinding.pendindRequestsRecyclerView.smoothScrollToPosition(0);
            AppAdapter.removeConsumerRequest(pendingTransaction);
            AppAdapter.updateIconLauncherBadge();
            //getAllPendingRequests(DEFAULT_PAGE_NUMBER);
            updatePendingRequestsExpireDate(AppAdapter.getPendingConsumerRequests());
        }, ConstantsUtils.TWO_HUNDRED_MILLISECONDS);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onNotificationHubConnected(NotificationHubConnectedEvent event) {
        AppAdapter.bus().removeStickyEvent(event);
        Timber.d("StandingByFragment: onNotificationHubConnected() ");
        loadAllData();
        AuthorizationDeeplink deeplink = ((AuthorizedActivity) getActivity()).getAuthorizationDeeplink();
        if (deeplink != null && deeplink.isValid()) {
            final Fragment fragment = AddConnectionFragment.newInstance(deeplink.getData());
            showChildFragment(fragment, fragment.getArguments());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGetMessagesEvent(GetMessagesEvent event) {
        Log.d("onGetMessagesEvent", "onGetMessagesEvent() called with: event = [" + event + "]");
        AppAdapter.bus().removeStickyEvent(event);
        loadAllData();
    }

    private void loadAllData() {
        showProgress();
        viewModel.loadAllData();
    }

    private void updatePendingRequestsExpireDate(List<TransactionEntity> pendingTransactions) {
        mDisposable = Observable.fromIterable(pendingTransactions)
                .map(pendingRequest -> pendingRequest.getValidTo() / ConstantsUtils.MILLISECONDS_IN_SECOND)
                .toList()
                .map(longs -> LogUtil.getGson().toJson(longs, ConstantsUtils.PENDING_REQUESTS_TYPE))
                .subscribeOn(Schedulers.io())
                .subscribe(longs -> AppAdapter.settings().savePendingRequest(longs));
    }

    private void postTransaction(TransactionEntity pendingTransaction, boolean accept) {
        final boolean shouldBiometricBeProvided = shouldBiometricBeProvided(
                pendingTransaction.getBiometric(),
                accept
        );
        final int cameraPermissionStatus = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.CAMERA
        );
        final boolean cameraPermissionGranted = cameraPermissionStatus == PERMISSION_GRANTED;
        if (shouldBiometricBeProvided && cameraPermissionGranted) {
            final ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance(
                    pendingTransaction,
                    accept
            );
            showChildFragment(fragment, fragment.getArguments());
        } else if (shouldBiometricBeProvided) {
            viewModel.setPostedTransaction(pendingTransaction, accept);
            requestPermissions(ConstantsUtils.CAMERA_PERMISSION, ConstantsUtils.CAMERA_REQUEST_CODE);
        } else {
            viewModel.postTransaction(pendingTransaction, accept, null);
        }
    }

    private boolean shouldBiometricBeProvided(@Nullable BiometricEntity biometric, boolean accept) {
        if (biometric == null) {
            return false;
        }
        return (BiometricTypeEntity.ALL == biometric.getBiometricType()) ||
                (BiometricTypeEntity.ACCEPT == biometric.getBiometricType() && accept) ||
                (BiometricTypeEntity.REJECT == biometric.getBiometricType() && !accept);
    }
}
