package com.covrsecurity.io.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.LoginActivityBinding;
import com.covrsecurity.io.event.VerificationCodeReceivedEvent;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.ui.dialog.YesNoDialog;
import com.covrsecurity.io.ui.fragment.interfaces.IUnregisteredFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.BaseUnauthorizedFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ScanFaceBiometricsFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.SplashFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.TutorialFragment;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.unauthorizedactivity.UnauthorizedActivityViewModel;
import com.covrsecurity.io.ui.viewmodel.unauthorizedactivity.UnauthorizedActivityViewModelFactory;
import com.covrsecurity.io.utils.ConstantsUtils;

import javax.inject.Inject;

import timber.log.Timber;

import static com.covrsecurity.io.ui.fragment.unauthorized.ScanFaceBiometricsFragment.SETUP_RECOVERY;
import static com.covrsecurity.io.utils.ConstantsUtils.CREATE_SMS_TOKEN_REQUEST_CODE;

public class UnauthorizedActivity extends BaseActivity<LoginActivityBinding, UnauthorizedActivityViewModel> {

    private static final String CANCEL_SETUP_TAG = "CANCEL_SETUP_TAG";

    @Inject
    UnauthorizedActivityViewModelFactory vmFactory;

    private boolean allowShowingBottomButtons = false;

    @Override
    protected int getLayoutId() {
        return R.layout.login_activity;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.frameLayoutFragmentContainer;
    }

    @NonNull
    @Override
    protected Class<UnauthorizedActivityViewModel> getViewModelClass() {
        return UnauthorizedActivityViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected void initViews() {
        super.initViews();
//        ScreenUtils.setupStatusBar(mBinding.sbLogin, this);
        initScreen();
        viewModel.clearDataLiveData.observe(this, new BaseObserver<>(
                null,
                response -> openGreetingScreen(),
                throwable -> {
                    Timber.e(throwable);
                    showErrToast(throwable);
                }
        ));
    }

    private void initScreen() {
        if (getIntent().getBooleanExtra(ConstantsUtils.INTENT_KEY_FOR_TUTORIAL_FRAGMENT, false)) {
            replaceFragment(TutorialFragment.newInstance(), null, false);
        } else {
            replaceFragment(SplashFragment.newInstance(), null, false);
        }
        mBinding.setCancelSetupClickListener(v1 -> showCancelSetupDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppAdapter.resetBadgeCount();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() == null || !intent.getExtras().containsKey("pdus")) {
            return;
        }
        SmsMessage[] messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messagesFromIntent != null) {
            for (SmsMessage pdu : messagesFromIntent) {
                String messageBody = pdu.getDisplayMessageBody();
                if (!TextUtils.isEmpty(messageBody) && messageBody.length() == 16) {
                    String code = messageBody.substring(0, 4);
                    AppAdapter.bus().postSticky(new VerificationCodeReceivedEvent(code));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCurrentFragment().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getCurrentFragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    public String getSmsToken() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent smsTokenPendingIntent = createSmsTokenPendingIntent();
            return smsManager.createAppSpecificSmsToken(smsTokenPendingIntent);
        } else {
            return null;
        }
    }

    public void showCancelSetupDialog() {
        boolean isRegistration = isRegistration();
        final int titleStringId = getTitleStringId(isRegistration);
        final DialogFragment newFragment = YesNoDialog.newInstance(
                titleStringId,
                R.string.cancel_dialog_message,
                new YesNoDialog.YesNoListener() {
                    @Override
                    public void onYesClicked() {
                        clearData();
                    }

                    @Override
                    public void onNoClicked() {
                    }
                });
        newFragment.show(getSupportFragmentManager(), CANCEL_SETUP_TAG);
    }

    public void clearData() {
        viewModel.clearData();
    }

    public void hideButtons() {
        mBinding.bottomButtonsLayout.setVisibility(View.GONE);
    }

    // this is used for smooth fragment transition
    public void makeButtonsInvisible() {
        if (doesAllowShowingBottomButtons()) {
            mBinding.bottomButtonsLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void showButtons() {
        if (doesAllowShowingBottomButtons()) {
            mBinding.bottomButtonsLayout.setVisibility(View.VISIBLE);
        }
    }

    public View getLeftButton() {
        return mBinding.leftBottomButton;
    }

    public View getRightButton() {
        return mBinding.rightBottomButton;
    }

    private void openGreetingScreen() {
        Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_SETUP_CANCEL);
        setAllowShowingBottomButtons(false);
        clearBackStack();
        initScreen();
    }

    @Override
    public void onBackPressed() {
        final Fragment currentFragment = getCurrentFragment();
        if (!(currentFragment instanceof BaseUnauthorizedFragment) && !(currentFragment instanceof ScanFaceBiometricsFragment)) {
            super.onBackPressed();
        } else if ((currentFragment instanceof BaseUnauthorizedFragment) && !((BaseUnauthorizedFragment) currentFragment).onBackButton()) {
            super.onBackPressed();
        } else if ((currentFragment instanceof ScanFaceBiometricsFragment) && !((ScanFaceBiometricsFragment) currentFragment).onBackButton()) { // todo reimplement
            super.onBackPressed();
        }
    }

    public void goBack() {
        super.onBackPressed();
    }

    private boolean doesAllowShowingBottomButtons() {
        return allowShowingBottomButtons;
    }

    public void setAllowShowingBottomButtons(boolean allowShowingBottomButtons) {
        this.allowShowingBottomButtons = allowShowingBottomButtons;
    }

    private PendingIntent createSmsTokenPendingIntent() {
        Intent intent = new Intent(this, UnauthorizedActivity.class);
        return PendingIntent.getActivity(this, CREATE_SMS_TOKEN_REQUEST_CODE, intent, 0);
    }

    private boolean isRegistration() {
        boolean isRegistration = true;
        final Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof IUnregisteredFragment) {
            final IUnregisteredFragment unregisteredFragment = (IUnregisteredFragment) currentFragment;
            isRegistration = unregisteredFragment.isRegistration();
        }
        return isRegistration;
    }

    @StringRes
    private int getTitleStringId(boolean isRegistration) {
        if (isRegistration) {
            return R.string.cancel_setup_dialog_title;
        } else {
            return R.string.cancel_recovery_dialog_title;
        }
    }
}
