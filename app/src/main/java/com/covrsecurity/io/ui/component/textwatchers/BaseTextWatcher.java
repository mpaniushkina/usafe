package com.covrsecurity.io.ui.component.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Lenovo on 25.05.2017.
 */

public class BaseTextWatcher implements TextWatcher {

    private IBeforeTextChangedListener mBeforeChangedListener;
    private ITextChangedListener mChangedListener;
    private IAfterTextChangedListener mAfterChangedListener;

    private boolean mFormatting;

    public BaseTextWatcher(IBeforeTextChangedListener beforeChangedListener) {
        mBeforeChangedListener = beforeChangedListener;
    }

    public BaseTextWatcher(ITextChangedListener changedListener) {
        mChangedListener = changedListener;
    }

    public BaseTextWatcher(IAfterTextChangedListener afterChangedListener) {
        mAfterChangedListener = afterChangedListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mBeforeChangedListener != null) {
            mBeforeChangedListener.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mChangedListener != null && !mFormatting) {
            mFormatting = true;
            mChangedListener.onTextChanged(s, start, before, count);
            mFormatting = false;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mAfterChangedListener != null) {
            mAfterChangedListener.afterTextChanged(s);
        }
    }
}
