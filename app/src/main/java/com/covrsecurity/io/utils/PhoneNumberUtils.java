package com.covrsecurity.io.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.covrsecurity.io.R;
import com.covrsecurity.io.model.Country;
import timber.log.Timber;

public class PhoneNumberUtils {

    private static String getSimCountryCode(final Context context) {
        return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso();
    }

    private static String getOperatorCountryCode(final Context context) {
        return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
    }

    private static Country getCountry(final Context context, final String countryCodeISO) {
        List<Country> countriesList = getCountriesJSON(context);
        if (countriesList != null) {
            for(Country country : countriesList) {
                if (country.getCountryCode().equalsIgnoreCase(countryCodeISO)) {
                    return country;
                }
            }
        }

//        if (BuildConfig.DEBUG) {
//            return new Country("Belarus", "BY", "+375");
//        } else {
            return new Country("Sweden", "SE", "+46");
//        }
    }

    public static Country getDefaultCounty(final Context context) {
        return getCountry(context, getSimCountryCode(context));
    }

    private static List<Country> getCountriesJSON(Context context) {
        InputStream stream = context.getResources().openRawResource(R.raw.countries_dialing_code);
        try {
            return parseCountries(new JSONObject(convertStreamToString(stream)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Country> parseCountries(JSONObject jsonCountries) {
        List<Country> countries = new ArrayList<>();
        Iterator<String> iter = jsonCountries.keys();

        while (iter.hasNext()) {
            String key = iter.next();
            try {
                countries.add(getCountryInfo(jsonCountries, key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(countries);
        return countries;
    }

    private static Country getCountryInfo(JSONObject jsonCountries, String key)
            throws JSONException {

        JSONObject countryJson = jsonCountries.getJSONObject(key);
        return new Country(countryJson.getString("name"), key, countryJson.getString("code"));
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String normalizePhoneNumber(String phone) {
        return "+" + phone.replaceAll("[^0-9]", "");
    }

    @SuppressLint("BinaryOperationInTimber")
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "");
            isValid = phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            Timber.e("NumberParseException was thrown: " + e.toString());
        }
        return isValid;
    }

}