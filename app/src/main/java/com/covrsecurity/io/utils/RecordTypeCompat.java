package com.covrsecurity.io.utils;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.covrsecurity.io.R;
import com.covrsecurity.io.greendao.model.database.RecordType;

/**
 * Created by Lenovo on 17.05.2017.
 */

public enum RecordTypeCompat {

    NOTE(R.string.covr_vault_item_notes,
            R.drawable.category_icon_group_notes_big,
            R.drawable.category_icon_note_big,
            R.drawable.category_icon_note_medium,
            R.drawable.category_icon_note_small),

    PAYMENT_CARD(R.string.covr_vault_item_payment_cards,
            R.drawable.category_icon_group_paymentcards_big,
            R.drawable.category_icon_paymentcard_big,
            R.drawable.category_icon_paymentcard_medium,
            R.drawable.category_icon_paymentcard_small);

    @StringRes
    private int mLabel;
    @DrawableRes
    private int mCategory;
    @DrawableRes
    private int mIconBig;
    @DrawableRes
    private int mIconMedium;
    @DrawableRes
    private int mIconSmall;

    RecordTypeCompat(int mLabel, int mCategory, int mIconBig, int mIconMedium, int mIconSmall) {
        this.mLabel = mLabel;
        this.mCategory = mCategory;
        this.mIconBig = mIconBig;
        this.mIconMedium = mIconMedium;
        this.mIconSmall = mIconSmall;
    }

    public int getLabel() {
        return mLabel;
    }

    public int getCategory() {
        return mCategory;
    }

    public int getIconBig() {
        return mIconBig;
    }

    public int getIconMedium() {
        return mIconMedium;
    }

    public int getIconSmall() {
        return mIconSmall;
    }

    public static RecordTypeCompat getTypeCompat(RecordType type) {
        switch (type) {
            case NOTE:
                return RecordTypeCompat.NOTE;
            case PAYMENT_CARD:
                return RecordTypeCompat.PAYMENT_CARD;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
