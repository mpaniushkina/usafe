package com.covrsecurity.io.ui.adapter.model;

import androidx.annotation.DrawableRes;

/**
 * Created by Lenovo on 16.05.2017.
 */

public class CovrVaultItemsListModel extends CovrVaultAbstractItemModel {

    @DrawableRes
    private int mIconRes;
    private String mTitle;
    private String mDescription;
    private long mDbRecordId;

    public CovrVaultItemsListModel(@DrawableRes int iconRes, String title, String description, long dbRecordId) {
        mIconRes = iconRes;
        mTitle = title;
        mDescription = description;
        mDbRecordId = dbRecordId;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public void setIconRes(int mIconRes) {
        this.mIconRes = mIconRes;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public long getDbRecordId() {
        return mDbRecordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CovrVaultItemsListModel that = (CovrVaultItemsListModel) o;

        if (mIconRes != that.mIconRes) return false;
        if (mTitle != null ? !mTitle.equals(that.mTitle) : that.mTitle != null) return false;
        return mDescription != null ? mDescription.equals(that.mDescription) : that.mDescription == null;

    }

    @Override
    public int hashCode() {
        int result = mIconRes;
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CovrVaultItemsListModel{" +
                "mIconRes=" + mIconRes +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                '}';
    }
}
