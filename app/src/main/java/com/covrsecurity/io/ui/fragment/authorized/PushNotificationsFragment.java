package com.covrsecurity.io.ui.fragment.authorized;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentPushNotificationsBinding;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.manager.SettingsManager;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.utils.DialogUtils;

import org.jetbrains.annotations.NotNull;

public class PushNotificationsFragment extends BaseFragment<FragmentPushNotificationsBinding> {

    private AlertDialog dialogAndroidSettings;

    public static Fragment newInstance() {
        return new PushNotificationsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_push_notifications;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.settings_push_notifications);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());
        mBinding.setPushSoundEnable(AppAdapter.settings().getPushSoundEnable());
        mBinding.setPushNotificationEnable(AppAdapter.settings().getPushNotificationsEnable());
        mBinding.setPushVibrateEnable(AppAdapter.settings().getPushVibrateEnable());
        mBinding.setPushNotificationEnableClickListener(v -> mBinding.settingsNotificationSwitch.performClick());
        mBinding.settingsNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppAdapter.settings().setPushNotificationsEnable(isChecked);
            mBinding.settingsSoundSwitch.setChecked(isChecked);
            mBinding.settingsVibrateSwitch.setChecked(isChecked);
            mBinding.settingsSoundSwitch.setEnabled(isChecked);
            mBinding.settingsVibrateSwitch.setEnabled(isChecked);
        });
        mBinding.setPushSoundEnableClickListener(v -> {
            if (mBinding.settingsSoundSwitch.isEnabled()) {
                mBinding.settingsSoundSwitch.performClick();
            }
        });
        mBinding.settingsSoundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppAdapter.settings().setPushSoundEnable(isChecked);
        });
        mBinding.setPushVibrateEnableClickListener(v -> {
            if (mBinding.settingsVibrateSwitch.isEnabled()) {
                mBinding.settingsVibrateSwitch.performClick();
            }
        });
        mBinding.settingsVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppAdapter.settings().setPushVibrateEnable(isChecked);
        });
        mBinding.setAndroidSettingsClickListener(v -> {
            if (NotificationManagerCompat.from(getActivity()).areNotificationsEnabled()) {
                dialogAndroidSettings = DialogUtils.showAlertDialog(getActivity(),
                        getString(R.string.settings_alert_title),
                        getString(R.string.settings_alert_message),
                        getString(R.string.proceed),
                        (dialog, which) -> openAppSettings(),
                        getString(R.string.cancel),
                        null, true);
            } else {
                openAppSettings();
            }
        });
    }

    private void openAppSettings() {
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //e.printStackTrace();
            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationManagerCompat nm = NotificationManagerCompat.from(getActivity());
        boolean notificationsEnabled = nm.areNotificationsEnabled();
        if (!notificationsEnabled) {
            mBinding.setPushNotificationEnable(false);
            mBinding.setPushSoundEnable(false);
            mBinding.setPushVibrateEnable(false);
        }
        mBinding.settingsNotificationSwitch.setEnabled(notificationsEnabled);
        mBinding.settingsSoundSwitch.setEnabled(notificationsEnabled);
        mBinding.settingsVibrateSwitch.setEnabled(notificationsEnabled);
    }

    @SuppressLint("NewApi")
    @Override
    public void onStop() {
        super.onStop();
        if (dialogAndroidSettings != null && dialogAndroidSettings.isShowing()) {
            dialogAndroidSettings.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        Bundle b = new Bundle();
        SettingsManager settings = AppAdapter.settings();
        b.putString("push_enabled", boolToStr(settings.getPushNotificationsEnable()));
        b.putString("push_sound", boolToStr(settings.getPushSoundEnable()));
        b.putString("push_vibration", boolToStr(settings.getPushVibrateEnable()));
        Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_SETTINGS_UPDATE, b);
        super.onDestroyView();
    }

    private String boolToStr(boolean v) {
        return v ? "1" : "0";
    }
}
