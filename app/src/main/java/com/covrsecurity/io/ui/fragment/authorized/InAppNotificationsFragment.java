package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.databinding.FragmentInappNotificationsBinding;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.manager.SettingsManager;
import com.covrsecurity.io.ui.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class InAppNotificationsFragment extends BaseFragment<FragmentInappNotificationsBinding> {

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
        mBinding.setInappIncomingSoundEnabled(AppAdapter.settings().getInappIncomingSoundEnabled());
        mBinding.setInappIncomingVibrateEnabled(AppAdapter.settings().getInappIncomingVibrationEnabled());
        mBinding.setInappResponseSoundEnabled(AppAdapter.settings().getInappResponseSoundEnabled());
        mBinding.setInappTimeoutSoundEnabled(AppAdapter.settings().getInappTimeoutSoundEnabled());
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
    }

    @Override
    public void onDestroyView() {
        Bundle b = new Bundle();
        SettingsManager settings = AppAdapter.settings();
        b.putString("inapp_incoming_sound", boolToStr(settings.getInappIncomingSoundEnabled()));
        b.putString("inapp_incoming_vibration", boolToStr(settings.getInappIncomingVibrationEnabled()));
        b.putString("inapp_response_sound", boolToStr(settings.getInappResponseSoundEnabled()));
        b.putString("inapp_timed_out_sound", boolToStr(settings.getInappTimeoutSoundEnabled()));
        b.putString("inapp_incoming_sound", boolToStr(settings.getInappIncomingSoundEnabled()));
        Analytics.logEvent(AppAdapter.context(), Analytics.EVENT_SETTINGS_UPDATE, b);
        super.onDestroyView();
    }

    private String boolToStr(boolean v) {
        return v ? "1" : "0";
    }
}
