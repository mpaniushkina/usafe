package com.covrsecurity.io.model.databinding;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.covrsecurity.io.BR;


/**
 * Created by Lenovo on 15.05.2017.
 */

public class CovrVaultViewCardModel extends BaseObservable {

    private boolean mShowCardNumber;
    private boolean mShowCvv;

    public CovrVaultViewCardModel() {
    }

    public CovrVaultViewCardModel(boolean showCardNumber, boolean showCvv) {
        mShowCardNumber = showCardNumber;
        mShowCvv = showCvv;
    }

    @Bindable
    public boolean isShowCardNumber() {
        return mShowCardNumber;
    }

    public void setShowCardNumber(boolean showCardNumber) {
        mShowCardNumber = showCardNumber;
        notifyPropertyChanged(BR.showCardNumber);
    }

    public void invertShowCardNumber() {
        setShowCardNumber(!mShowCardNumber);
    }

    @Bindable
    public boolean isShowCvv() {
        return mShowCvv;
    }

    public void setShowCvv(boolean showCvv) {
        notifyPropertyChanged(BR.showCvv);
        mShowCvv = showCvv;
    }

    public void invertShowCvv() {
        setShowCvv(!mShowCvv);
    }

    @Override
    public String toString() {
        return "CovrVaultViewCardModel{" +
                "mShowCardNumber=" + mShowCardNumber +
                ", mShowCvv=" + mShowCvv +
                '}';
    }
}
