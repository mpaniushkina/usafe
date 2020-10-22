package com.covrsecurity.io.model.databinding;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.os.Parcelable;

import com.covrsecurity.io.BR;
import com.covrsecurity.io.greendao.model.database.RecordType;

/**
 * Created by Lenovo on 01.06.2017.
 */

public abstract class CovrVaultBaseModel extends BaseObservable implements Parcelable {

    protected String mDescription;
    protected boolean mFavorite;

    public CovrVaultBaseModel() {
    }

    public CovrVaultBaseModel(String description, boolean favorite) {
        mDescription = description;
        mFavorite = favorite;
    }

    @Bindable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
        notifyPropertyChanged(BR.favorite);
    }

    public boolean invertFavorite() {
        boolean newFavorite = !mFavorite;
        setFavorite(newFavorite);
        return newFavorite;
    }

    public abstract String getAdditional();

    public abstract RecordType getRecordType();
}
