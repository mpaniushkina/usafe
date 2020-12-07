package com.covrsecurity.io.ui.fragment.authorized;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentSettingsBinding;
import com.covrsecurity.io.ui.adapter.SettingsAdapter;
import com.covrsecurity.io.ui.fragment.BaseParentFragment;
import com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeInfoFragment;
import com.covrsecurity.io.ui.viewmodel.base.observer.BaseObserver;
import com.covrsecurity.io.ui.viewmodel.biometricsshared.BiometricsSharedViewModel;
import com.covrsecurity.io.ui.viewmodel.biometricsshared.BiometricsSharedViewModelFactory;
import com.covrsecurity.io.ui.viewmodel.settings.SettingsViewModel;
import com.covrsecurity.io.ui.viewmodel.settings.SettingsViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import timber.log.Timber;

public class SettingsFragment extends BaseParentFragment<FragmentSettingsBinding, SettingsViewModel>
        implements SettingsAdapter.ItemClickListener {

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Inject
    SettingsViewModelFactory vmFactory;
    @Inject
    BiometricsSharedViewModelFactory sharedVmFactory;

    private BiometricsSharedViewModel sharedViewModel;
    private BiometricsSharedViewModel registerRecoveryLiveData;

    private AlertDialog dialogAndroidSettings;
    private boolean mReadyToUseFingerprintScanner;
    private boolean mOnViewBounded;
    private SettingsAdapter adapter;

    public static final int ABOUT_ITEM = 0;
    public static final int USE_FINGERPRINT_ITEM = 1;
    public static final int PUSH_NOTIFICATIONS_ITEM = 2;
    public static final int IN_APP_NOTIFICATIONS_ITEM = 3;
    public static final int LANGUAGE_ITEM = 4;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @NonNull
    @Override
    protected Class<SettingsViewModel> getViewModelClass() {
        return SettingsViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.registerBiometricRecoveryLiveData.observe(this, new BaseObserver<>(
                this::showProgress,
                response -> {
                    hideProgress();
                     showToast(R.string.recovery_set_up_success);
                },
                throwable -> {
                    hideProgress();
                    Timber.e(throwable);
                    showErrToast(throwable);
                }
        ));
        sharedViewModel = ViewModelProviders.of(getActivity(), sharedVmFactory).get(BiometricsSharedViewModel.class);
        sharedViewModel.registerRecoveryLiveData.observe(this, new BaseObserver<>(
                null,
                response -> {
                    closeChildFragment();
                    registerRecoveryRequest(response.getBiometricsBytes(), response.getImageIdCard());
                },
                throwable -> Timber.e(throwable)
        ));
    }

    @SuppressLint("NewApi")
    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setChangeCodeClickListener(v -> {
            showChildFragment();
        });
//        String readMoreString = getString(R.string.read_more);
//        mBinding.fingerprintReadMore.setMovementMethod(LinkMovementMethod.getInstance());
//        mBinding.fingerprintReadMore.setText(StringUtils.getClickableSpannable(
//                getString(R.string.settings_fingerprint_tip, readMoreString), readMoreString,
//                () -> {
//                    Fragment fragment = FingerprintReadMoreChildFragment.newInstance(getString(R.string.cfg_about_fingerprint));
//                    showChildFragment(fragment, fragment.getArguments());
//                }));
//        mBinding.setDataCollectionEnable(AppAdapter.settings().getDataCollectionEnable());
//        mBinding.setKbSoundEnabled(AppAdapter.settings().getKeyboardSoundEnabled());
//        mBinding.setKbVibrateEnabled(AppAdapter.settings().getKeyboardVibrationEnabled());
//        mBinding.setInappIncomingSoundEnabled(AppAdapter.settings().getInappIncomingSoundEnabled());
//        mBinding.setInappIncomingVibrateEnabled(AppAdapter.settings().getInappIncomingVibrationEnabled());
//        mBinding.setInappResponseSoundEnabled(AppAdapter.settings().getInappResponseSoundEnabled());
//        mBinding.setInappTimeoutSoundEnabled(AppAdapter.settings().getInappTimeoutSoundEnabled());
//
//        mBinding.setDataCollectionEnableClickListener(v -> mBinding.settingsDataCollectionSwitch.performClick());
//        mBinding.settingsDataCollectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setDataCollectionEnable(isChecked);
//        });
//
//        mReadyToUseFingerprintScanner = FingerprintUtils.getInstance(getActivity()).readyToUseFingerprintScanner(getActivity());
//        mBinding.addOnRebindCallback(new OnRebindCallback<FragmentSettingsBinding>() {
//            @Override
//            public void onBound(FragmentSettingsBinding binding) {
//                super.onBound(binding);
//                mOnViewBounded = true;
//            }
//        });
//        setUseFingerprint();
//        mBinding.setPushSoundEnable(AppAdapter.settings().getPushSoundEnable());
//        mBinding.setPushNotificationEnable(AppAdapter.settings().getPushNotificationsEnable());
//        mBinding.setPushVibrateEnable(AppAdapter.settings().getPushVibrateEnable());
//        mBinding.setUseFingerprintClickListener(v -> mBinding.settingsFingerprintSwitch.performClick());
//        mBinding.settingsFingerprintSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (!mOnViewBounded) {
//                return;
//            }
//            if (mReadyToUseFingerprintScanner && isChecked) {
//                Fragment fragment = ChangeCodeFragment.newInstance(true);
//                showChildFragment(fragment, fragment.getArguments());
//            } else if (mReadyToUseFingerprintScanner) {
//                AppAdapter.settings().setUseFingerprintAuth(false);
//            } else {
//                AppAdapter.settings().setUseFingerprintAuth(false);
//                mBinding.settingsFingerprintSwitch.setChecked(false);
//                showChildFragment(FingerprintErrorMessageFragment.newInstance(), null);
//            }
//        });
//        mBinding.setPushNotificationEnableClickListener(v -> mBinding.settingsNotificationSwitch.performClick());
//        mBinding.settingsNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setPushNotificationsEnable(isChecked);
//            mBinding.settingsSoundSwitch.setChecked(isChecked);
//            mBinding.settingsVibrateSwitch.setChecked(isChecked);
//            mBinding.settingsSoundSwitch.setEnabled(isChecked);
//            mBinding.settingsVibrateSwitch.setEnabled(isChecked);
//        });
//        mBinding.setPushSoundEnableClickListener(v -> {
//            if (mBinding.settingsSoundSwitch.isEnabled()) {
//                mBinding.settingsSoundSwitch.performClick();
//            }
//        });
//        mBinding.settingsSoundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setPushSoundEnable(isChecked);
//        });
//        mBinding.setPushVibrateEnableClickListener(v -> {
//            if (mBinding.settingsVibrateSwitch.isEnabled()) {
//                mBinding.settingsVibrateSwitch.performClick();
//            }
//        });
//        mBinding.settingsVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setPushVibrateEnable(isChecked);
//        });
//
//        // KEYBOARD
//        mBinding.setKbSoundEnabledClickListener(v -> mBinding.settingsKeyboardSoundSwitch.performClick());
//        mBinding.settingsKeyboardSoundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setKeyboardSoundEnabled(isChecked);
//        });
//        mBinding.setKbVibrateEnabledClickListener(v -> mBinding.settingsKeyboardVibrateSwitch.performClick());
//        mBinding.settingsKeyboardVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setKeyKeyboardVibrationEnabled(isChecked);
//        });
//
//        // IN-APP NOTIFICATIONS
//        mBinding.setInAppIncomingSoundEnabledClickListener(v -> mBinding.settingsInappReqSound.performClick());
//        mBinding.settingsInappReqSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setInappIncomingSoundEnabled(isChecked);
//        });
//        mBinding.setInAppIncomingVibrateEnabledClickListener(v -> mBinding.settingsInappReqVibration.performClick());
//        mBinding.settingsInappReqVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setInappIncomingVibrationEnabled(isChecked);
//        });
//        mBinding.setInAppResponseSoundEnabledClickListener(v -> mBinding.settingsInappReqResponseSound.performClick());
//        mBinding.settingsInappReqResponseSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setInappResponseSoundEnabled(isChecked);
//        });
//        mBinding.setInAppTimeoutSoundEnabledClickListener(v -> mBinding.settingsInappTimeoutSound.performClick());
//        mBinding.settingsInappTimeoutSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppAdapter.settings().setInappTimeoutSoundEnabled(isChecked);
//        });
//
//        mBinding.setAndroidSettingsClickListener(v -> {
//            if (NotificationManagerCompat.from(getActivity()).areNotificationsEnabled()) {
//                dialogAndroidSettings = DialogUtils.showAlertDialog(getActivity(),
//                        getString(R.string.settings_alert_title),
//                        getString(R.string.settings_alert_message),
//                        getString(R.string.proceed),
//                        (dialog, which) -> openAppSettings(),
//                        getString(R.string.cancel),
//                        null, true);
//            } else {
//                openAppSettings();
//            }
//        });
//        mBinding.setSetUpRecoveryClickListener(view -> {
////            Intent intent = new Intent(getActivity(), UnauthorizedActivity.class);
////            boolean setUpRecoveryAction = true;
////            intent.putExtra(SETUP_RECOVERY, setUpRecoveryAction);
////            startActivity(intent);
//            final ScanFaceBiometricsFragment fragment = ScanFaceBiometricsFragment.newInstance();
//            showChildFragment(fragment, fragment.getArguments());
//        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.settings);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());

        ArrayList<String> settingName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.setting_names)));
        mBinding.settingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SettingsAdapter(getContext(), settingName);
        adapter.setClickListener(this);
        mBinding.settingsRecyclerView.setAdapter(adapter);
    }

    private void registerRecoveryRequest(byte[] biometricsData, byte[] imageIdCard) {
        viewModel.registerRecoveryRequest(biometricsData, imageIdCard);
    }

//    private void openAppSettings() {
//        try {
//            //Open the specific App Info page:
//            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            //e.printStackTrace();
//            //Open the generic Apps page:
//            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
//            startActivity(intent);
//        }
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        NotificationManagerCompat nm = NotificationManagerCompat.from(getActivity());
//        boolean notificationsEnabled = nm.areNotificationsEnabled();
//        if (!notificationsEnabled) {
//            mBinding.setPushNotificationEnable(false);
//            mBinding.setPushSoundEnable(false);
//            mBinding.setPushVibrateEnable(false);
//        }
//        mBinding.settingsNotificationSwitch.setEnabled(notificationsEnabled);
//        mBinding.settingsSoundSwitch.setEnabled(notificationsEnabled);
//        mBinding.settingsVibrateSwitch.setEnabled(notificationsEnabled);
//    }

//    private void setUseFingerprint() {
//        boolean fingerprintAuthUses = AppAdapter.settings().getFingerprintAuthUses();
//        mBinding.setUseFingerprint(fingerprintAuthUses && mReadyToUseFingerprintScanner);
//    }

//    @SuppressLint("NewApi")
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (dialogAndroidSettings != null && dialogAndroidSettings.isShowing()) {
//            dialogAndroidSettings.dismiss();
//        }
//    }

    @Override
    protected int getFragmentContainerId() {
//        return mBinding.settingChildFramgnetContainer.getId();
        return 0;
    }

    @Override
    public void showChildFragment() {
        ChangeCodeInfoFragment fragment = ChangeCodeInfoFragment.newInstance(false);
        showChildFragment(fragment, fragment.getArguments());
    }

    public void showChildFragment(Fragment fragment, Bundle args) {
//        mBinding.settingsSlidingUpPanel.clearAnimation();
//        mBinding.childFragmentTopShadow.clearAnimation();
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
        removeChildFragments();
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
//        mBinding.settingsSlidingUpPanel.startAnimation(bottomUp);
//        mBinding.settingsSlidingUpPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeChildFragment() {
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);
//        fadeOut.setAnimationListener(new AnimationEndListner() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.up_bottom);
//                mBinding.settingsSlidingUpPanel.startAnimation(bottomUp);
//                mBinding.settingsSlidingUpPanel.setVisibility(View.GONE);
//                ActivityUtils.scheduleOnMainThread(() -> {
//                    if (!isStateSaved()) {
//                        getChildFragmentManager().popBackStack();
//                    }
//                }, ConstantsUtils.HALF_SECOND);
//            }
//        });
//        setUseFingerprint();
//        mBinding.childFragmentTopShadow.startAnimation(fadeOut);
//        mBinding.childFragmentTopShadow.setVisibility(View.INVISIBLE);
    }


    @Override
    protected int getTitleId() {
        return R.string.settings;
    }

//    @Override
//    public void onDestroyView() {
//        Bundle b = new Bundle();
//        SettingsManager settings = AppAdapter.settings();
//        b.putString("push_enabled", boolToStr(settings.getPushNotificationsEnable()));
//        b.putString("push_sound", boolToStr(settings.getPushSoundEnable()));
//        b.putString("push_vibration", boolToStr(settings.getPushVibrateEnable()));
//        b.putString("inapp_incoming_sound", boolToStr(settings.getInappIncomingSoundEnabled()));
//        b.putString("inapp_incoming_vibration", boolToStr(settings.getInappIncomingVibrationEnabled()));
//        b.putString("inapp_response_sound", boolToStr(settings.getInappResponseSoundEnabled()));
//        b.putString("inapp_timed_out_sound", boolToStr(settings.getInappTimeoutSoundEnabled()));
//        b.putString("inapp_incoming_sound", boolToStr(settings.getInappIncomingSoundEnabled()));
//        b.putString("personal_code_sound", boolToStr(settings.getKeyboardSoundEnabled()));
//        b.putString("personal_code_vibration", boolToStr(settings.getKeyboardVibrationEnabled()));
//        Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_SETTINGS_UPDATE, b);
//        super.onDestroyView();
//    }
//
//    private String boolToStr(boolean v) {
//        return v ? "1" : "0";
//    }

    @Override
    public void onItemClick(View view, int position) {
        Fragment fragment = null;
        switch (position) {
            case ABOUT_ITEM:
                fragment = AboutFragment.newInstance();
                break;
            case USE_FINGERPRINT_ITEM:
                fragment = FingerprintFragment.newInstance();
                break;
            case PUSH_NOTIFICATIONS_ITEM:
                fragment = PushNotificationsFragment.newInstance();
                break;
            case IN_APP_NOTIFICATIONS_ITEM:
                fragment = InAppNotificationsFragment.newInstance();
                break;
//            case LANGUAGE_ITEM:
//                fragment = HelpFragment.newInstance();
//                break;
        }
        replaceFragment(fragment, fragment.getArguments(), true);
    }
}
