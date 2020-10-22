package com.covrsecurity.io.ui.component.textwatchers;

import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.covrsecurity.io.utils.ConstantsUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Lenovo on 08.06.2017.
 */

public class ExpireDateTextWatcher implements TextWatcher {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMyy", Locale.US);

    protected boolean isUpdating;
    protected String mOldString = "";
    protected String mMask;
    @NonNull
    private final EditText mEditText;

    public ExpireDateTextWatcher(@NonNull EditText editText, String mask) {
        mEditText = editText;
        mMask = mask;
        DATE_FORMAT.setLenient(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = s.toString()
                .substring(0, s.length())
                .replaceAll("\\D", "");
        StringBuilder mask = new StringBuilder();
        if (isUpdating) {
            mOldString = str;
            isUpdating = false;
            return;
        }
        int i = 0;
        for (char m : mMask.toCharArray()) {
            if (m != ConstantsUtils.DEFAULT_ITEM_PLACEHOLDER) {
                mask.append(m);
                continue;
            }
            try {
                if(i == 0 && needAddZero(str)) {
                    mask.append("0").append(str);
                } else if(isMonthValid(str)) {
                    mask.append(str.charAt(i));
                } else {
                    mask.append(mOldString);
                    break;
                }
            } catch (Exception e) {
                break;
            }
            i++;
        }
        isUpdating = true;
        String text = mask.toString().trim();
        mEditText.setText(text);
        mEditText.setSelection(text.length());
    }

    private boolean needAddZero(final String text) {
        if(text.length() == 1) {
            int digit = parseInt(text);
            return digit > 1 &&  digit <= 9;
        } else {
            return false;
        }
    }

    private boolean isMonthValid(final String text) throws IllegalArgumentException {
        switch (text.length()) {
            case 1:
                int digit = parseInt(text);
                return digit == 0 || digit == 1;
            case 2:
                int digits = parseInt(text);
                return digits > 0 && digits <= 12;
            default:
                return true;
        }
    }

    private int parseInt(final String digit) throws IllegalArgumentException {
        if(!TextUtils.isEmpty(digit)) {
            return Integer.parseInt(digit);
        } else {
            return 0;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

}