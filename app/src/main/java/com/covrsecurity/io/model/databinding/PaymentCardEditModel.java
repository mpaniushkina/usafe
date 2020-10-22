package com.covrsecurity.io.model.databinding;

import androidx.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.covrsecurity.io.BR;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.utils.CardType;
import com.google.gson.Gson;

/**
 * Created by Lenovo on 10.05.2017.
 */

public class PaymentCardEditModel extends CovrVaultBaseModel {

    public static final Parcelable.Creator<PaymentCardEditModel> CREATOR = new Parcelable.Creator<PaymentCardEditModel>() {
        @Override
        public PaymentCardEditModel createFromParcel(Parcel source) {
            return new PaymentCardEditModel(source);
        }

        @Override
        public PaymentCardEditModel[] newArray(int size) {
            return new PaymentCardEditModel[size];
        }
    };

    private CardType mCardType;
    private String mFullName;
    private String mCardNumber;
    private String mExpDate;
    private String mCvvNumber;
    private String mBankName;
    private String mPhoneNumber;
    private String mNotes;

    public PaymentCardEditModel() {
        super();
    }

    public PaymentCardEditModel(CardType cardType, String description, boolean favorite, String fullName, String cardNumber,
                                String expDate, String cvvNumber, String bankName, String phoneNumber, String notes) {
        super(description, favorite);
        this.mCardType = cardType;
        this.mFullName = fullName;
        this.mCardNumber = cardNumber;
        this.mExpDate = expDate;
        this.mCvvNumber = cvvNumber;
        this.mBankName = bankName;
        this.mPhoneNumber = phoneNumber;
        this.mNotes = notes;
    }

    protected PaymentCardEditModel(Parcel in) {
        int tmpMCardType = in.readInt();
        this.mCardType = tmpMCardType == -1 ? null : CardType.values()[tmpMCardType];
        this.mDescription = in.readString();
        this.mFavorite = in.readByte() != 0;
        this.mFullName = in.readString();
        this.mCardNumber = in.readString();
        this.mExpDate = in.readString();
        this.mCvvNumber = in.readString();
        this.mBankName = in.readString();
        this.mPhoneNumber = in.readString();
        this.mNotes = in.readString();
    }

    @Bindable
    public CardType getCardType() {
        return mCardType;
    }

    public void setCardType(CardType cardType) {
        mCardType = cardType;
        notifyPropertyChanged(BR.cardType);
    }

    @Bindable
    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
        notifyPropertyChanged(BR.fullName);
    }

    @Bindable
    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
        notifyPropertyChanged(BR.cardNumber);
    }

    @Bindable
    public String getExpDate() {
        return mExpDate;
    }

    public void setExpDate(String expDate) {
        mExpDate = expDate;
        notifyPropertyChanged(BR.expDate);
    }

    @Bindable
    public String getCvvNumber() {
        return mCvvNumber;
    }

    public void setCvvNumber(String cvvNumber) {
        mCvvNumber = cvvNumber;
        notifyPropertyChanged(BR.cvvNumber);
    }

    @Bindable
    public String getBankName() {
        return mBankName;
    }

    public void setBankName(String bankName) {
        mBankName = bankName;
        notifyPropertyChanged(BR.bankName);
    }

    @Bindable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
        notifyPropertyChanged(BR.notes);
    }

    @Override
    public String getAdditional() {
        return new Gson().toJson(mCardType != null ? mCardType : CardType.UNKNOWN);
    }

    @Override
    public RecordType getRecordType() {
        return RecordType.PAYMENT_CARD;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCardType == null ? -1 : this.mCardType.ordinal());
        dest.writeString(this.mDescription);
        dest.writeByte(this.mFavorite ? (byte) 1 : (byte) 0);
        dest.writeString(this.mFullName);
        dest.writeString(this.mCardNumber);
        dest.writeString(this.mExpDate);
        dest.writeString(this.mCvvNumber);
        dest.writeString(this.mBankName);
        dest.writeString(this.mPhoneNumber);
        dest.writeString(this.mNotes);
    }

    @Override
    public String toString() {
        return "PaymentCardEditModel{" +
                "mCardType=" + mCardType +
                ", mDescription='" + mDescription + '\'' +
                ", mFavorite=" + mFavorite +
                ", mFullName='" + mFullName + '\'' +
                ", mCardNumber='" + mCardNumber + '\'' +
                ", mExpDate='" + mExpDate + '\'' +
                ", mCvvNumber='" + mCvvNumber + '\'' +
                ", mBankName='" + mBankName + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mNotes='" + mNotes + '\'' +
                '}';
    }
}
