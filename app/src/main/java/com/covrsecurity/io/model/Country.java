package com.covrsecurity.io.model;

import java.io.Serializable;
import java.util.Locale;

public class Country implements Serializable, Comparable<Country> {

    private String mCountry;
    private String mCountryCode;
    private String mPhoneCode;

    public Country() {}

    public Country(String country, String countryCode, String phoneCode) {
        this.mCountry = country;
        this.mCountryCode = countryCode;
        this.mPhoneCode = phoneCode;
    }

    @Override
    public int compareTo(Country another) {
        return this.getCountry().toLowerCase(Locale.ENGLISH)
                .compareTo(another.getCountry().toLowerCase(Locale.ENGLISH));
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getPhoneCode() {
        return mPhoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        mPhoneCode = phoneCode;
    }
}
