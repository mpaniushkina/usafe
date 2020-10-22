package com.covrsecurity.io.model;

/**
 * Created by Lenovo on 18.05.2017.
 */

public class EncryptedEnvelope {

    public String mKey;
    public String mValue;

    public EncryptedEnvelope() {
    }

    public EncryptedEnvelope(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncryptedEnvelope envelope = (EncryptedEnvelope) o;

        if (mKey != null ? !mKey.equals(envelope.mKey) : envelope.mKey != null) return false;
        return mValue != null ? mValue.equals(envelope.mValue) : envelope.mValue == null;

    }

    @Override
    public int hashCode() {
        int result = mKey != null ? mKey.hashCode() : 0;
        result = 31 * result + (mValue != null ? mValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EncryptedEnvelope{" +
                "mKey='" + mKey + '\'' +
                ", mValue='" + mValue + '\'' +
                '}';
    }
}

