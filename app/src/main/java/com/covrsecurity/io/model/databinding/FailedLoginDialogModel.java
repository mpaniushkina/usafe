package com.covrsecurity.io.model.databinding;

import android.content.res.Resources;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.covrsecurity.io.BR;
import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;

public class FailedLoginDialogModel extends BaseObservable {

    @ColorRes
    private int mTitleColor;
    private String mTitleText;
    @ColorRes
    private int mAttemptNumberColor;
    @ColorRes
    private int mWarningFooterColor;
    private int mAttemptsLeft;
    private CharSequence mWarningTextSpannable;
    private String[] mHighlightedAttemptsText;
    private boolean mIsTryAgainClickable;
    private String mTryAgainText;

    public FailedLoginDialogModel() {
    }

    public FailedLoginDialogModel(int attemptsLeft, boolean isLogin) {
        this.mAttemptsLeft = attemptsLeft;
        boolean lastAttempt = mAttemptsLeft == 1;

        mTitleColor = lastAttempt ? R.color.red : R.color.covr_green;
        mAttemptNumberColor = lastAttempt ? R.color.red : R.color.covr_green;
        mWarningFooterColor = lastAttempt ? R.color.red : R.color.text_color_black;

        mTitleText = isLogin ?
                getString(R.string.failed_attempts_login_to_covr_failed) :
                getString(R.string.failed_attempts_change_pin);
        mHighlightedAttemptsText = lastAttempt ?
                getStrings(R.string.failed_attempts_warning_text_highlighted_chunk_2, R.string.failed_attempts_warning_text_highlighted_chunk_3) :
                getStrings(R.string.failed_attempts_warning_text_highlighted_chunk_1);

        @StringRes int warningTextId = lastAttempt ? R.string.failed_attempts_warning_text_last : R.string.failed_attempts_warning_text_5_2;
        mWarningTextSpannable = getSpannableText(getString(warningTextId), mHighlightedAttemptsText, AppAdapter.resources().getColor(R.color.red));
    }

    @Bindable
    public int getTitleColor() {
        return mTitleColor;
    }

    public void setTitleColor(int titleColor) {
        this.mTitleColor = titleColor;
        notifyPropertyChanged(BR.titleColor);
    }

    @Bindable
    public int getAttemptNumberColor() {
        return mAttemptNumberColor;
    }

    public void setAttemptNumberColor(int attemptNumberColor) {
        this.mAttemptNumberColor = attemptNumberColor;
        notifyPropertyChanged(BR.attemptNumberColor);
    }

    @Bindable
    public int getWarningFooterColor() {
        return mWarningFooterColor;
    }

    public void setWarningFooterColor(int warningFooterColor) {
        this.mWarningFooterColor = warningFooterColor;
        notifyPropertyChanged(BR.warningFooterColor);
    }

    @Bindable
    public int getAttemptsLeft() {
        return mAttemptsLeft;
    }

    public void setAttemptsLeft(int attemptsLeft) {
        this.mAttemptsLeft = attemptsLeft;
        notifyPropertyChanged(BR.attemptsLeft);
    }

    @Bindable
    public CharSequence getWarningTextSpannable() {
        return mWarningTextSpannable;
    }

    public void setWarningTextSpannable(CharSequence textSpannable) {
        this.mWarningTextSpannable = textSpannable;
        notifyPropertyChanged(BR.warningTextSpannable);
    }

    @Bindable
    public String[] getHighlightedAttemptsText() {
        return mHighlightedAttemptsText;
    }

    public void setHighlightedAttemptsText(String[] highlightedAttemptsText) {
        this.mHighlightedAttemptsText = highlightedAttemptsText;
        notifyPropertyChanged(BR.highlightedAttemptsText);
    }

    @Bindable
    public boolean isIsTryAgainClickable() {
        return mIsTryAgainClickable;
    }

    public void setIsTryAgainClickable(boolean isTryAgainClickable) {
        this.mIsTryAgainClickable = isTryAgainClickable;
        notifyPropertyChanged(BR.isTryAgainClickable);
    }

    @Bindable
    public String getTryAgainText() {
        return mTryAgainText;
    }

    public void setTryAgainText(String tryAgainText) {
        this.mTryAgainText = tryAgainText;
        notifyPropertyChanged(BR.tryAgainText);
    }

    private String getString(@StringRes int id) {
        return AppAdapter.resources().getString(id);
    }

    private String[] getStrings(@StringRes int... ids) {
        Resources resources = AppAdapter.resources();
        String[] array = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            array[i] = resources.getString(ids[i]);
        }
        return array;
    }

    private Spannable getSpannableText(String text, String[] spans, @ColorInt int color) {
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan foregroundSpan;
        for (String part : spans) {
            int start = text.indexOf(part);
            foregroundSpan = new ForegroundColorSpan(color);
            spannableString.setSpan(foregroundSpan, start, start + part.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
}
