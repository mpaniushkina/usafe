package com.covrsecurity.io.ui.adapter.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.covrsecurity.io.utils.RecordTypeCompat;

/**
 * Created by Lenovo on 15.05.2017.
 */

public class CovrVaultBottomListItemModel extends CovrVaultAbstractItemModel {

    @StringRes
    private int mTitle;
    @DrawableRes
    private int mIcon;
    private RecordTypeCompat mType;

    public CovrVaultBottomListItemModel(int title, int icon, RecordTypeCompat type) {
        mTitle = title;
        mIcon = icon;
        mType = type;
    }

    public int getTitle() {
        return mTitle;
    }

    public void setTitle(int mTitle) {
        this.mTitle = mTitle;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    public RecordTypeCompat getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "CovrVaultBottomListItemModel{" +
                "mTitle=" + mTitle +
                ", mIcon=" + mIcon +
                '}';
    }
}
