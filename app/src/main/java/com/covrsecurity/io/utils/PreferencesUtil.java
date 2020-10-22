package com.covrsecurity.io.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesUtil {

    public static final String TAG = PreferencesUtil.class.getSimpleName();
    public static final String COMPANY_PUBLIC_KEY = "COMPANY_PUBLIC_KEY";

    private final SharedPreferences settings;
    private final DefaultEncryptHelper defaultEncryptHelper;

    public PreferencesUtil(final Context context, final String prefName) {
        settings = context.getSharedPreferences(prefName, MODE_PRIVATE);
        defaultEncryptHelper = new DefaultEncryptHelper();
    }

    public DefaultEncryptHelper getDefaultEncryptHelper() {
        return defaultEncryptHelper;
    }

    protected void setString(String key, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    protected void setStringCiphered(String key, byte[] value) {
        try {
            setString(key, defaultEncryptHelper.encryptRSABytes(value));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void setInt(String key, int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    protected void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    protected void setFloat(String key, float value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    protected void setLong(String key, long value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    protected void setStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    protected void setDouble(String key, double value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    protected String getString(String key, String defaultValue) {
        return settings.getString(key, defaultValue);
    }

    protected byte[] getCipheredBytes(String key) {
        try {
            String string = getString(key, "");
            if (TextUtils.isEmpty(string)) {
                return null;
            } else {
                return defaultEncryptHelper.decryptRSABytes(string);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    protected int getInt(String key, int defaultValue) {
        return settings.getInt(key, defaultValue);
    }

    protected boolean getBoolean(String key, boolean defaultValue) {
        return settings.getBoolean(key, defaultValue);
    }

    protected float getFloat(String key, float defaultValue) {
        return settings.getFloat(key, defaultValue);
    }

    protected long getLong(String key, long defaultValue) {
        return settings.getLong(key, defaultValue);
    }

    protected Set<String> getStringSet(String key, Set<String> defaultValue) {
        return settings.getStringSet(key, defaultValue);
    }

    protected double getDouble(String key, double defaultValue) {
        return Double.longBitsToDouble(settings.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public void drop(String key) {
        settings.edit().remove(key).commit();
    }

    public void dropAll() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void storeCompanyPublicKey(String companyRegId, byte[] companyPublicKey) {
        HashMap<String, byte[]> map = getStringByteHashMap(null, COMPANY_PUBLIC_KEY);
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(companyRegId, companyPublicKey);
        saveStringByteHashMap(map, COMPANY_PUBLIC_KEY);
    }

    public byte[] fetchCompanyPublicKey(String companyRegId) {
        HashMap<String, byte[]> map = getStringByteHashMap(null, COMPANY_PUBLIC_KEY);
        if (map == null) {
            return new byte[]{};
        }
        if (map.containsKey(companyRegId)) {
            return map.get(companyRegId);
        } else {
            String errorMessage = "Failed to fetch timestamp for companyRegId: " + companyRegId;
            Timber.e(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private void saveHashMap(HashMap<String, String> list, String key){
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json).apply();
    }

    private HashMap<String, String> getHashMap(Context context, String key){
        Gson gson = new Gson();
        String json = settings.getString(key, null);
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveStringByteHashMap(HashMap<String, byte[]> list, String key){
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json).apply();
    }

    private HashMap<String, byte[]> getStringByteHashMap(Context context, String key){
        Gson gson = new Gson();
        String json = settings.getString(key, null);
        Type type = new TypeToken<HashMap<String, byte[]>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
