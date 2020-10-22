package com.covrsecurity.io.manager;

import android.content.Context;
import android.util.Pair;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.utils.PreferencesUtil;

public class SettingsManager extends PreferencesUtil {

    private final static String KEY_USE_FINGERPRINT_AUTH = "KEY_USE_FINGERPRINT_AUTH";
    private final static String KEY_FINGERPRINT_AUTH_TOO_MANY_ATTEMPTS = "KEY_FINGERPRINT_AUTH_TOO_MANY_ATTEMPTS";
    private final static String KEY_SOUND_ENABLE = "KEY_SOUND_ENABLE";
    private final static String KEY_DATA_COLLECTION_ENABLE = "KEY_DATA_COLLECTION";
    private final static String KEY_NOTIFICATIONS_ENABLE = "KEY_NOTIFICATIONS_ENABLE";
    private final static String KEY_VIBRATE_ENABLE = "KEY_VIBRATE_ENABLE";
    private final static String KEY_PUSH_TOKEN = "KEY_PUSH_TOKEN";

    private final static String KEY_KEYBOARD_VIBRATION_ENABLED = "KEY_KEYBOARD_VIBRATION_ENABLED";
    private final static String KEY_KEYBOARD_SOUND_ENABLED = "KEY_KEYBOARD_SOUND_ENABLED";

    private final static String KEY_INAPP_INCOM_VIBRATION_ENABLED = "KEY_INAPP_INCOM_VIBRATION_ENABLED";
    private final static String KEY_INAPP_INCOM_SOUND_ENABLED = "KEY_INAPP_INCOM_SOUND_ENABLED";
    private final static String KEY_INAPP_RESPONSE_SOUND_ENABLED = "KEY_INAPP_RESPONSE_SOUND_ENABLED";
    private static final String KEY_INAPP_TIMEOUT_SOUND_ENABLED = "KEY_INAPP_TIMEOUT_SOUND_ENABLED";

    @Deprecated
    private static final String KEY_WAS_CONFLICT = "KEY_WAS_CONFLICT";
    private static final String KEY_HISTORY_BADGE = "KEY_HISTORY_BADGE";
    private static final String KEY_FINGERPRINT_PASSWORD = "KEY_FINGERPRINT_PASSWORD";
    private static final String KEY_FINGERPRINT_IV = "KEY_FINGERPRINT_IV";
    private static final String KEY_PENDING_REQUESTS = "KEY_PENDING_REQUESTS";
    private static final String KEY_CURRENT_BADGE_COUNT = "KEY_CURRENT_BADGE_COUNT";
    private static final String KEY_LAST_TIME_HISTORY_VIEWED = "KEY_LAST_TIME_HISTORY_VIEWED";

    public SettingsManager(Context context) {
        super(context, BuildConfig.APPLICATION_ID);
    }

    public void setKeyFingerprintAuthTooManyAttempts(boolean manyAttempts) {
        setBoolean(KEY_FINGERPRINT_AUTH_TOO_MANY_ATTEMPTS, manyAttempts);
    }

    public boolean getFingerprintAuthTooManyAttempts() {
        return getBoolean(KEY_FINGERPRINT_AUTH_TOO_MANY_ATTEMPTS, false);
    }

    public void setPushSoundEnable(boolean enable) {
        setBoolean(KEY_SOUND_ENABLE, enable);
    }

    public void setDataCollectionEnable(boolean enable) {
        setBoolean(KEY_DATA_COLLECTION_ENABLE, enable);
    }

    public boolean getDataCollectionEnable() {
        return getBoolean(KEY_DATA_COLLECTION_ENABLE, true);
    }

    public void setUseFingerprintAuth(boolean useFingerprintAuth) {
        setBoolean(KEY_USE_FINGERPRINT_AUTH, useFingerprintAuth);
    }

    public boolean getFingerprintAuthUses() {
        return getBoolean(KEY_USE_FINGERPRINT_AUTH, false);
    }

    public void setPushNotificationsEnable(boolean enable) {
        setBoolean(KEY_NOTIFICATIONS_ENABLE, enable);
    }

    public void setPushVibrateEnable(boolean enable) {
        setBoolean(KEY_VIBRATE_ENABLE, enable);
    }

    public boolean getPushSoundEnable() {
        return getBoolean(KEY_SOUND_ENABLE, true);
    }

    public boolean getPushNotificationsEnable() {
        return getBoolean(KEY_NOTIFICATIONS_ENABLE, true);
    }

    public boolean getPushVibrateEnable() {
        return getBoolean(KEY_VIBRATE_ENABLE, true);
    }

    @Deprecated
    public void setPushToken(String pushToken) {
        setString(KEY_PUSH_TOKEN, pushToken);
    }

    @Deprecated
    public void setWasConflict(boolean wasConflict) {
        setBoolean(KEY_WAS_CONFLICT, wasConflict);
    }

    @Deprecated
    public boolean wasConflict() {
        return getBoolean(KEY_WAS_CONFLICT, false);
    }

    // Digital keyboard settings
    public boolean getKeyboardSoundEnabled() {
        return getBoolean(KEY_KEYBOARD_SOUND_ENABLED, true);
    }

    public void setKeyboardSoundEnabled(boolean enable) {
        setBoolean(KEY_KEYBOARD_SOUND_ENABLED, enable);
    }

    public void setKeyKeyboardVibrationEnabled(boolean enable) {
        setBoolean(KEY_KEYBOARD_VIBRATION_ENABLED, enable);
    }

    public boolean getKeyboardVibrationEnabled() {
        return getBoolean(KEY_KEYBOARD_VIBRATION_ENABLED, false);
    }

    // In-app notifications settings
    public boolean getInappIncomingSoundEnabled() {
        return getBoolean(KEY_INAPP_INCOM_SOUND_ENABLED, true);
    }

    public void setInappIncomingSoundEnabled(boolean enable) {
        setBoolean(KEY_INAPP_INCOM_SOUND_ENABLED, enable);
    }

    public void setInappIncomingVibrationEnabled(boolean enable) {
        setBoolean(KEY_INAPP_INCOM_VIBRATION_ENABLED, enable);
    }

    public boolean getInappIncomingVibrationEnabled() {
        return getBoolean(KEY_INAPP_INCOM_VIBRATION_ENABLED, true);
    }

    public void setInappResponseSoundEnabled(boolean enable) {
        setBoolean(KEY_INAPP_RESPONSE_SOUND_ENABLED, enable);
    }

    public boolean getInappResponseSoundEnabled() {
        return getBoolean(KEY_INAPP_RESPONSE_SOUND_ENABLED, true);
    }

    public void setInappTimeoutSoundEnabled(boolean enable) {
        setBoolean(KEY_INAPP_TIMEOUT_SOUND_ENABLED, enable);
    }

    public boolean getInappTimeoutSoundEnabled() {
        return getBoolean(KEY_INAPP_TIMEOUT_SOUND_ENABLED, true);
    }

    public void setHistoryBadge(int badge) {
        setInt(KEY_HISTORY_BADGE, badge);
    }

    public int getHistoryBadge() {
        return getInt(KEY_HISTORY_BADGE, 0);
    }

    public void saveFingerprintPasswordIv(String base64password, String base64IV) {
        setString(KEY_FINGERPRINT_PASSWORD, base64password);
        setString(KEY_FINGERPRINT_IV, base64IV);
    }

    public void clearFingerprintPasswordIv() {
        drop(KEY_FINGERPRINT_PASSWORD);
        drop(KEY_FINGERPRINT_IV);
    }

    public Pair<String, String> getFingerprintPasswordIv() {
        return new Pair<>(getString(KEY_FINGERPRINT_PASSWORD, null), getString(KEY_FINGERPRINT_IV, null));
    }

    public void savePendingRequest(String value) {
        setString(KEY_PENDING_REQUESTS, value);
    }

    public String getPendingRequest() {
        return getString(KEY_PENDING_REQUESTS, "");
    }

    public void setCurrentBadgeCount(int badgeCount) {
        setInt(KEY_CURRENT_BADGE_COUNT, badgeCount);
    }

    public int getCurrentBadgeCount() {
        return getInt(KEY_CURRENT_BADGE_COUNT, 0);
    }

    public void setLastTimeHistoryViewed(long lastTimeViewedMillis) {
        setLong(KEY_LAST_TIME_HISTORY_VIEWED, lastTimeViewedMillis);
    }

    public long getLastTimeHistoryViewed() {
        return getLong(KEY_LAST_TIME_HISTORY_VIEWED, 0);
    }
}
