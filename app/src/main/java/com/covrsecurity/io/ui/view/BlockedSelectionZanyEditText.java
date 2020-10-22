package com.covrsecurity.io.ui.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Lenovo on 26.05.2017.
 */

public class BlockedSelectionZanyEditText extends ZanyEditText {

    public BlockedSelectionZanyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BlockedSelectionZanyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockedSelectionZanyEditText(Context context) {
        super(context);
    }

    @Override
    public void onSelectionChanged(int start, int end) {
        CharSequence text = getText();
        if (text != null) {
            if (start != text.length() || end != text.length()) {
                setSelection(text.length(), text.length());
                return;
            }
        }
        super.onSelectionChanged(start, end);
    }
}
