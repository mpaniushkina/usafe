package com.covrsecurity.io.model.databinding;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.text.TextUtils;

import com.covrsecurity.io.model.RegistrationPush;
import com.covrsecurity.io.utils.SizedStack;

/**
 * Created by Egor on 31.08.2016.
 */
public class EnterVerificationCodeModel extends BaseObservable {

    private final int MAX_DIGITS;
    public SizedStack<Character> digits;
    public RegistrationPush status;
    public boolean isRetryButtonEnabled;

    public EnterVerificationCodeModel(int maxDigits) {
        MAX_DIGITS = maxDigits;
        digits = new SizedStack<>(MAX_DIGITS);
    }

    public EnterVerificationCodeModel(SizedStack<Character> digits, RegistrationPush status, boolean isRetryButtonEnabled) {
        this.digits = digits;
        this.status = status;
        this.isRetryButtonEnabled = isRetryButtonEnabled;
        MAX_DIGITS = digits.size();
    }

    @Bindable
    public SizedStack<Character> getDigits() {
        return digits;
    }

    public void setDigits(SizedStack<Character> mDigits) {
        this.digits = mDigits;
    }

    @Bindable
    public RegistrationPush getStatus() {
        return status;
    }

    public void setStatus(RegistrationPush mStatus) {
        this.status = mStatus;
        notifyPropertyChanged(com.covrsecurity.io.BR.status);
    }

    @Bindable
    public boolean isRetryButtonEnabled() {
        return isRetryButtonEnabled;
    }

    public void setRetryButtonEnabled(boolean retryButtonEnabled) {
        isRetryButtonEnabled = retryButtonEnabled;
        notifyPropertyChanged(com.covrsecurity.io.BR.retryButtonEnabled);
    }

    public Character getDigitSafe(int position) {
        return digits.getSafe(position);
    }

    public void addDigit(Character value) {
        getDigits().push(value);
        notifyPropertyChanged(com.covrsecurity.io.BR.digits);
    }

    public void remoteDigit() {
        getDigits().pop();
        notifyPropertyChanged(com.covrsecurity.io.BR.digits);
    }

    public String digitsToString() {
        return TextUtils.join("", digits);
    }

    public int getDigitsNumber() {
        return digits.size();
    }

}