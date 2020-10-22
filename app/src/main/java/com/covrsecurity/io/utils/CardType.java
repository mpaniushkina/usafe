package com.covrsecurity.io.utils;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import android.text.TextUtils;

import com.covrsecurity.io.R;

/**
 * Created by Lenovo on 19.05.2017.
 */

public enum CardType {

    UNKNOWN(null, R.string.covr_vault_payment_card_edit_main_title, R.drawable.category_icon_paymentcard_big, R.drawable.category_icon_paymentcard_medium, R.drawable.category_icon_paymentcard_small),
    VISA("^4[0-9]{12}(?:[0-9]{3})?$", R.string.payment_card_visa, R.drawable.category_icon_visa_big, R.drawable.category_icon_visa_medium, R.drawable.category_icon_visa_small),
    MASTERCARD("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$", R.string.payment_card_mastercard, R.drawable.category_icon_mastercard_big, R.drawable.category_icon_mastercard_medium, R.drawable.category_icon_mastercard_small),
    AMERICAN_EXPRESS("^3[47][0-9]{0,13}", R.string.payment_card_american_express, R.drawable.category_icon_americanexpress_big, R.drawable.category_icon_americanexpress_medium, R.drawable.category_icon_americanexpress_small),
    DINERS_CLUB("^3(?:0[0-5]|[68][0-9])[0-9]{11}$", R.string.payment_card_diners_club, R.drawable.category_icon_dinersclub_big, R.drawable.category_icon_dinersclub_medium, R.drawable.category_icon_dinersclub_small),
    DISCOVER("^6(?:011|5[0-9]{1,2})[0-9]{0,12}", R.string.payment_card_discover, R.drawable.category_icon_discover_big, R.drawable.category_icon_discover_medium, R.drawable.category_icon_discover_small),
    JCB("^(?:2131|1800|35\\d{3})\\d{11}$", R.string.payment_card_jcb, R.drawable.category_icon_jcb_big, R.drawable.category_icon_jcb_medium, R.drawable.category_icon_jcb_small),
    MAESTRO("^(5018|5020|5038|6304|6759|6761|6763)[0-9]{8,15}$", R.string.payment_card_maestro, R.drawable.category_icon_maestro_big, R.drawable.category_icon_maestro_medium, R.drawable.category_icon_maestro_small),
    UNION_PAY("^62\\d{12,17}$", R.string.payment_card_union_pay, R.drawable.category_icon_unionpay_big, R.drawable.category_icon_unionpay_medium, R.drawable.category_icon_unionpay_small),
    DKBANK("^(5019)\\d{12}$", R.string.payment_card_dkbank, R.drawable.category_icon_dkbank_big, R.drawable.category_icon_dkbank_medium, R.drawable.category_icon_dkbank_small),
    MIR("^(2200|2201|2202|2203|2204)\\d{12}$", R.string.payment_card_mir, R.drawable.category_icon_mir_big, R.drawable.category_icon_mir_medium, R.drawable.category_icon_mir_small),
    UATP("^(1)\\d{14}$", R.string.payment_card_uatp, R.drawable.category_icon_uatp_big, R.drawable.category_icon_uatp_medium, R.drawable.category_icon_uatp_small);
//    VERVE("^(506099|5061[0-8][0-9]|50619[0-8]65000[2-9]|65001[0-9]|65002[0-7])\\d{10,13}$", R.string.payment_card_verve, R.drawable.category_icon_verve_big, R.drawable.category_icon_verve_medium, R.drawable.category_icon_verve_small);

    //    private Pattern mPattern;
    private String mPattern;
    @StringRes
    private int mCardIssuer;
    @DrawableRes
    private int mBigIcon;
    @DrawableRes
    private int mMediumIcon;
    @DrawableRes
    private int mSmallIcon;

    CardType(String pattern, int cardIssuer, int bigIcon, int mediumIcon, int smallIcon) {
        mPattern = pattern;
        mCardIssuer = cardIssuer;
        mBigIcon = bigIcon;
        mMediumIcon = mediumIcon;
        mSmallIcon = smallIcon;
    }

    @StringRes
    public int getCardIssuer() {
        return mCardIssuer;
    }

    @DrawableRes
    public int getBigIcon() {
        return mBigIcon;
    }

    @DrawableRes
    public int getMediumIcon() {
        return mMediumIcon;
    }

    @DrawableRes
    public int getSmallIcon() {
        return mSmallIcon;
    }

    public static CardType detect(String cardNumber) {
        for (CardType cardType : CardType.values()) {
            if (null == cardType.mPattern || TextUtils.isEmpty(cardNumber)) {
                continue;
            }
            if (cardNumber.startsWith("4")) {
                return VISA;
            }
            if (cardNumber.matches(cardType.mPattern)) {
                return cardType;
            }
        }
        return UNKNOWN;
    }

    public boolean validate(final String str) {
        return !TextUtils.isEmpty(str) && validateLunh(str) && mPattern != null && str.matches(mPattern);
    }

    private boolean validateLunh(final String str) {
        final int offset = str.length() - 1;
        int sum = 0;
        for (int i = 0; i <= offset; i++) {
            final int d = Character.digit(str.charAt(offset - i), 10);

            if (d < 0) {
                return false;
            }
            final int v = d + (i % 2) * d;
            sum += v / 10 + v % 10;
        }
        return sum % 10 == 0;
    }
}
