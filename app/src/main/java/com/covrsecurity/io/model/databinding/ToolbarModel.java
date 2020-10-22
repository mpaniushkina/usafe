package com.covrsecurity.io.model.databinding;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import android.view.View;

import com.covrsecurity.io.BR;

/**
 * Created by Lenovo on 10.05.2017.
 */

public class ToolbarModel extends BaseObservable {

    @StringRes
    private int mLeftButtonText;
    @DrawableRes
    private int mLeftImageResId;
    @StringRes
    private int mTitleText;
    @DrawableRes
    private int mRightImageResId;
    @StringRes
    private int mRightButtonText;
    @ColorRes
    private int mBackgroundColor;
    @ColorRes
    private int mTitleTextColor;
    private int mLayoutVisibility;
    private View.OnClickListener mLeftButtonClick;
    private View.OnClickListener mLeftImageClick;
    private View.OnClickListener mRightButtonClick;
    private View.OnClickListener mRightImageClick;

    private ToolbarModel(@StringRes int leftButtonText, @DrawableRes int leftImageResId,
                         @StringRes int titleText, @DrawableRes int rightImageResId,
                         @StringRes int rightButtonText, @ColorRes int backgroundColor,
                         @ColorRes int titleTextColor, int visibility,
                         View.OnClickListener leftButtonClick, View.OnClickListener leftImageClick,
                         View.OnClickListener rightButtonClick, View.OnClickListener rightImageClick) {
        mLeftButtonText = leftButtonText;
        mLeftImageResId = leftImageResId;
        mTitleText = titleText;
        mRightImageResId = rightImageResId;
        mRightButtonText = rightButtonText;
        mBackgroundColor = backgroundColor;
        mTitleTextColor = titleTextColor;
        mLayoutVisibility = visibility;
        mLeftButtonClick = leftButtonClick;
        mLeftImageClick = leftImageClick;
        mRightButtonClick = rightButtonClick;
        mRightImageClick = rightImageClick;
    }

    @Bindable
    public int getLeftButtonText() {
        return mLeftButtonText;
    }

    public void setLeftButtonText(int leftButtonText) {
        mLeftButtonText = leftButtonText;
        notifyPropertyChanged(BR.leftButtonText);
    }

    @Bindable
    public int getLeftImageResId() {
        return mLeftImageResId;
    }

    public void setLeftImageResId(int leftImageResId) {
        mLeftImageResId = leftImageResId;
        notifyPropertyChanged(BR.leftImageResId);
    }

    @Bindable
    public int getTitleText() {
        return mTitleText;
    }

    public void setTitleText(int titleText) {
        mTitleText = titleText;
        notifyPropertyChanged(BR.titleText);
    }

    @Bindable
    public int getRightImageResId() {
        return mRightImageResId;
    }

    public void setRightImageResId(int rightImageResId) {
        mRightImageResId = rightImageResId;
        notifyPropertyChanged(BR.rightImageResId);
    }

    @Bindable
    public int getRightButtonText() {
        return mRightButtonText;
    }

    public void setRightButtonText(int rightButtonText) {
        mRightButtonText = rightButtonText;
        notifyPropertyChanged(BR.rightButtonText);
    }

    @Bindable
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        notifyPropertyChanged(BR.backgroundColor);
    }

    @Bindable
    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        mTitleTextColor = titleTextColor;
        notifyPropertyChanged(BR.titleTextColor);
    }

    @Bindable
    public int getLayoutVisibility() {
        return mLayoutVisibility;
    }

    public void setLayoutVisibility(int visibility) {
        mLayoutVisibility = visibility;
        notifyPropertyChanged(BR.layoutVisibility);
    }

    @Bindable
    public View.OnClickListener getLeftButtonClick() {
        return mLeftButtonClick;
    }

    public void setLeftButtonClick(View.OnClickListener mLeftButtonClick) {
        this.mLeftButtonClick = mLeftButtonClick;
        notifyPropertyChanged(BR.leftButtonClick);
    }

    @Bindable
    public View.OnClickListener getLeftImageClick() {
        return mLeftImageClick;
    }

    public void setLeftImageClick(View.OnClickListener mLeftImageClick) {
        this.mLeftImageClick = mLeftImageClick;
        notifyPropertyChanged(BR.leftImageClick);
    }

    @Bindable
    public View.OnClickListener getRightButtonClick() {
        return mRightButtonClick;
    }

    public void setRightButtonClick(View.OnClickListener mRightButtonClick) {
        this.mRightButtonClick = mRightButtonClick;
        notifyPropertyChanged(BR.rightButtonClick);
    }

    @Bindable
    public View.OnClickListener getRightImageClick() {
        return mRightImageClick;
    }

    public void setRightImageClick(View.OnClickListener mRightImageClick) {
        this.mRightImageClick = mRightImageClick;
        notifyPropertyChanged(BR.rightImageClick);
    }

    public static class Builder {
        private int leftButtonText;
        private int leftImageResId;
        private int titleText;
        private int rightImageResId;
        private int rightButtonText;
        private int backgroundColor;
        private int titleTextColor;
        private int mLayoutVisibility;
        private View.OnClickListener mLeftButtonClick;
        private View.OnClickListener mLeftImageClick;
        private View.OnClickListener mRightButtonClick;
        private View.OnClickListener mRightImageClick;

        public Builder setLeftButtonText(@StringRes int leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public Builder setLeftImageResId(@DrawableRes int leftImageResId) {
            this.leftImageResId = leftImageResId;
            return this;
        }

        public Builder setTitleText(@StringRes int titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder setRightImageResId(@DrawableRes int rightImageResId) {
            this.rightImageResId = rightImageResId;
            return this;
        }

        public Builder setRightButtonText(@StringRes int rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        public Builder setBackgroundColor(@ColorRes int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setTitleTextColor(@ColorRes int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public Builder setVisibility(int visibility) {
            this.mLayoutVisibility = visibility;
            return this;
        }

        public Builder setLeftButtonClick(View.OnClickListener leftButtonClick) {
            this.mLeftButtonClick = leftButtonClick;
            return this;
        }

        public Builder setLeftImageClick(View.OnClickListener leftImageClick) {
            this.mLeftImageClick = leftImageClick;
            return this;
        }

        public Builder setRightButtonClick(View.OnClickListener rightButtonClick) {
            this.mRightButtonClick = rightButtonClick;
            return this;
        }

        public Builder setRightImageClick(View.OnClickListener rightImageClick) {
            this.mRightImageClick = rightImageClick;
            return this;
        }

        public ToolbarModel create() {
            return new ToolbarModel(leftButtonText, leftImageResId, titleText, rightImageResId,
                    rightButtonText, backgroundColor, titleTextColor, mLayoutVisibility,
                    mLeftButtonClick, mLeftImageClick, mRightButtonClick, mRightImageClick);
        }
    }
}
