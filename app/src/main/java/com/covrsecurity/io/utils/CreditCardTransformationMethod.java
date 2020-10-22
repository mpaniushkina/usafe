package com.covrsecurity.io.utils;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by Lenovo on 08.06.2017.
 */

public class CreditCardTransformationMethod extends PasswordTransformationMethod {

    private static final int LAST_ALWAYS_VISIBLE_COUNT = 4;
    private static char DOT = '\u2022';

    private static CreditCardTransformationMethod sInstance;

    public static CreditCardTransformationMethod getInstance() {
        if (sInstance != null)
            return sInstance;

        sInstance = new CreditCardTransformationMethod();
        return sInstance;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        String digitsOnly = source.toString().replaceAll("\\D", "");
        CharSequence visible;
        if (digitsOnly.length() > LAST_ALWAYS_VISIBLE_COUNT) {
            visible = digitsOnly.subSequence(digitsOnly.length() - LAST_ALWAYS_VISIBLE_COUNT, digitsOnly.length());
        } else {
            visible = "";
        }
        return new PasswordCharSequence(source, visible);
    }

    private class PasswordCharSequence implements CharSequence {

        private CharSequence mAllSymbols;
        private CharSequence mVisible;

        public PasswordCharSequence(CharSequence allSymbols, CharSequence visible) {
            mAllSymbols = allSymbols;
            mVisible = visible;
        }

        public char charAt(int index) {
            if (length() > LAST_ALWAYS_VISIBLE_COUNT && index < length() - LAST_ALWAYS_VISIBLE_COUNT) {
                return DOT;
            } else {
                return mVisible.charAt(LAST_ALWAYS_VISIBLE_COUNT - (mAllSymbols.length() - index));
            }
        }

        public int length() {
            return mAllSymbols.length();
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return mAllSymbols.subSequence(start, end);
        }
    }
}
