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
import com.covrsecurity.io.databinding.FragmentInappNotificationsBinding;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.utils.DialogUtils;

import org.jetbrains.annotations.NotNull;

public class InAppNotificationsFragment extends BaseFragment<FragmentInappNotificationsBinding> {

    private AlertDialog dialogAndroidSettings;

    public static Fragment newInstance() {
        return new InAppNotificationsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_inapp_notifications;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.settings_inapp_notifications);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());

        mBinding.setInAppIncomingSoundEnabledClickListener(v -> mBinding.settingsInappReqSound.performClick());
        mBinding.settingsInappReqSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppAdapter.settings().setInappIncomingSoundEnabled(isChecked);
        });
        mBinding.setInAppIncomingVibrateEnabledClickListener(v -> mBinding.settingsInappReqVibration.performClick());
        mBinding.settingsInappReqVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppAdapter.settings().setInappIncomingVibrationEnabled(isChecked);
        });
        mBinding.setInAppResponseSoundEnabledClickListener(v -> mBinding.settingsInappReqResponseSound.performClick());
        mBinding.settingsInappReqResponseSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppAdapter.settings().setInappResponseSoundEnabled(isChecked);
        });
        mBinding.setInAppTimeoutSoundEnabledClickListener(v -> mBinding.settingsInappTimeoutSound.performClick());
        mBinding.settingsInappTimeoutSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppAdapter.settings().setInappTimeoutSoundEnabled(isChecked);
        });

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

    @SuppressLint("NewApi")
    @Override
    public void onStop() {
        super.onStop();
        if (dialogAndroidSettings != null && dialogAndroidSettings.isShowing()) {
            dialogAndroidSettings.dismiss();
        }
    }
}
