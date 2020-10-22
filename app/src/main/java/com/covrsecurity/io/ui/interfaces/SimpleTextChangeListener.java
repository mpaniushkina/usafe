package com.covrsecurity.io.ui.interfaces;

import android.text.Editable;
import android.text.TextWatcher;

public interface SimpleTextChangeListener extends TextWatcher {

    void beforeTextChanged(CharSequence s, int start, int count, int after);

    void onTextChanged(CharSequence s, int start, int before, int count);

    void afterTextChanged(Editable s);
}
