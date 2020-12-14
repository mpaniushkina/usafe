package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentSplashBinding;
import com.covrsecurity.io.event.InternetLostEvent;
import com.covrsecurity.io.event.InternetRegainedEvent;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.splash.SplashViewModel;
import com.covrsecurity.io.ui.viewmodel.splash.SplashViewModelFactory;
import com.covrsecurity.io.utils.ConnectivityUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import timber.log.Timber;

public class SplashFragment extends BaseUnauthorizedViewModelFragment<FragmentSplashBinding, SplashViewModel> {

    public static final String TAG = SplashFragment.class.getCanonicalName();

    public static Fragment newInstance() {
        return new SplashFragment();
    }

    @Inject
    SplashViewModelFactory vmFactory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_splash;
    }

    @NonNull
    @Override
    protected Class<SplashViewModel> getViewModelClass() {
        return SplashViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    public boolean onBackButton() {
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.authLiveData.observe(this, new BaseObserver<>(
                null,
                this::handleAuth,
                this::showAuthError
        ));
    }

    @Override
    public void onStart() {
        super.onStart();
        AppAdapter.bus().register(this);
        if (!ConnectivityUtils.isConnected(AppAdapter.context())) {
            showNoInternetDlg((dialog, which) -> doAuth());
        } else {
            doAuth();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        AppAdapter.bus().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void internetRegained(InternetRegainedEvent event) {
        AppAdapter.bus().removeStickyEvent(event);
        doAuth();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void internetLost(InternetLostEvent event) {
        AppAdapter.bus().removeStickyEvent(event);
        showNoInternetDlg((dialog, which) -> doAuth());
    }

    private void doAuth() {
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing() && activity instanceof UnauthorizedActivity) {
            viewModel.doAuth(activity);
        }
    }

    private void handleAuth(Boolean isRegistered) {
        if (isRegistered) {
            performLogin();
        } else {
            performRegister();
        }
    }

    private void performLogin() {
        startAuthorizedActivity(false);
    }

    private void performRegister() {
        FragmentActivity a = getActivity();
        if (a != null && !a.isFinishing() && a instanceof UnauthorizedActivity) {
            replaceFragment(TutorialFragment.newInstance(), null, false);
        }
    }

    private void showAuthError(Throwable throwable) {
        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        Timber.e(throwable);
    }
}
