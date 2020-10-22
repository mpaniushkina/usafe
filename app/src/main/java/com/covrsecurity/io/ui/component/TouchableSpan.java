package com.covrsecurity.io.ui.component;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.covrsecurity.io.utils.ActivityUtils;

/**
 * Created by Lenovo on 05.01.2017.
 */

public abstract class TouchableSpan extends ClickableSpan {

    private boolean mIsPressed;
    private int mPressedBackgroundColor;
    private int mNormalTextColor;
    private int mPressedTextColor;
    private Runnable mOnClickAction;

    public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
    }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    public void setOnClickAction(Runnable onClickAction) {
        mOnClickAction = onClickAction;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
        ds.bgColor = mIsPressed ? mPressedBackgroundColor : 0xFFFFFF;
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        if (mOnClickAction != null) {
            ActivityUtils.runOnMainThread(mOnClickAction);
        }
    }
}