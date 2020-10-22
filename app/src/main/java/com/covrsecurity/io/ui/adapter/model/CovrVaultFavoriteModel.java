package com.covrsecurity.io.ui.adapter.model;

import androidx.annotation.DrawableRes;

import com.covrsecurity.io.greendao.model.database.RecordType;

/**
 * Created by Lenovo on 16.05.2017.
 */

public class CovrVaultFavoriteModel extends CovrVaultAbstractItemModel {

    @DrawableRes
    private int mIconRes;
    private String mTitle;
    private long mDbRecordId;
    private RecordType mType;

    public CovrVaultFavoriteModel(@DrawableRes int iconRes, String title, long dbRecordId, RecordType type) {
        mIconRes = iconRes;
        mTitle = title;
        mDbRecordId = dbRecordId;
        mType = type;
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

    public long getDbRecordId() {
        return mDbRecordId;
    }

    public RecordType getType() {
        return mType;
    }

    public void setType(RecordType type) {
        mType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CovrVaultFavoriteModel that = (CovrVaultFavoriteModel) o;

        if (mIconRes != that.mIconRes) return false;
        if (mDbRecordId != that.mDbRecordId) return false;
        if (mTitle != null ? !mTitle.equals(that.mTitle) : that.mTitle != null) return false;
        return mType == that.mType;

    }

    @Override
    public int hashCode() {
        int result = mIconRes;
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + (int) (mDbRecordId ^ (mDbRecordId >>> 32));
        result = 31 * result + (mType != null ? mType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CovrVaultFavoriteModel{" +
                "mIconRes=" + mIconRes +
                ", mTitle='" + mTitle + '\'' +
                ", mDbRecordId=" + mDbRecordId +
                ", mType=" + mType +
                '}';
    }
}
