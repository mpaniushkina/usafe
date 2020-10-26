package com.covrsecurity.io.model.databinding;

import androidx.databinding.Bindable;

import android.os.Parcel;
import android.os.Parcelable;

import com.covrsecurity.io.greendao.model.database.RecordType;

/**
 * Created by Lenovo on 29.05.2017.
 */

public class CovrVaultEditNoteModel extends CovrVaultBaseModel {

    public static final Parcelable.Creator<CovrVaultEditNoteModel> CREATOR = new Parcelable.Creator<CovrVaultEditNoteModel>() {
        @Override
        public CovrVaultEditNoteModel createFromParcel(Parcel source) {
            return new CovrVaultEditNoteModel(source);
        }

        @Override
        public CovrVaultEditNoteModel[] newArray(int size) {
            return new CovrVaultEditNoteModel[size];
        }
    };

    private String mContent;

    public CovrVaultEditNoteModel() {
        super();
    }

    public CovrVaultEditNoteModel(String description, String content, boolean favorite) {
        super(description, favorite);
        mContent = content;
    }

    protected CovrVaultEditNoteModel(Parcel in) {
        this.mDescription = in.readString();
        this.mContent = in.readString();
        this.mFavorite = in.readByte() != 0;
    }

    @Bindable
    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
        notifyPropertyChanged(com.covrsecurity.io.BR.content);
    }

    @Override
    public String getAdditional() {
        return null;
    }

    @Override
    public RecordType getRecordType() {
        return RecordType.NOTE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDescription);
        dest.writeString(this.mContent);
        dest.writeByte(this.mFavorite ? (byte) 1 : (byte) 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CovrVaultEditNoteModel that = (CovrVaultEditNoteModel) o;

        if (mFavorite != that.mFavorite) return false;
        if (mDescription != null ? !mDescription.equals(that.mDescription) : that.mDescription != null)
            return false;
        return mContent != null ? mContent.equals(that.mContent) : that.mContent == null;
    }

    @Override
    public int hashCode() {
        int result = mDescription != null ? mDescription.hashCode() : 0;
        result = 31 * result + (mContent != null ? mContent.hashCode() : 0);
        result = 31 * result + (mFavorite ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CovrVaultEditNoteModel{" +
                "mDescription='" + mDescription + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mFavorite=" + mFavorite +
                '}';
    }
}
