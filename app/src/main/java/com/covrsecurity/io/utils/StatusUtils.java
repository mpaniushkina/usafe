package com.covrsecurity.io.utils;

import android.content.Context;

import com.covrsecurity.io.R;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;

import static com.covrsecurity.io.domain.entity.StatusEntity.ACCEPTED;
import static com.covrsecurity.io.domain.entity.StatusEntity.REJECTED;
import static com.covrsecurity.io.utils.ConstantsUtils.POSITIVE_BUTTON_TEXT_OK;
import static com.covrsecurity.io.utils.ConstantsUtils.POSITIVE_BUTTON_TEXT_YES;

/**
 * Created by Lenovo on 26.01.2017.
 */

public class StatusUtils {

    public static String getStatusText(Context context, TransactionEntity pendingTransaction) {
        if (pendingTransaction.getRequest() != null && (POSITIVE_BUTTON_TEXT_YES.equalsIgnoreCase(pendingTransaction.getRequest().getAccept()) ||
                POSITIVE_BUTTON_TEXT_OK.equalsIgnoreCase(pendingTransaction.getRequest().getAccept()))) {
            if (ACCEPTED.equals(pendingTransaction.getStatus())) {
                return context.getString(R.string.history_details_accepted);
            }
            if (REJECTED.equals(pendingTransaction.getStatus())) {
                return context.getString(R.string.history_details_cancelled);
            }
        }
        StatusEntity status = pendingTransaction.getStatus();
        if (status == null) {
            return context.getString(R.string.history_details_undefined);
        }
        switch (status) {
            case ACCEPTED:
            case REJECTED:
                String buttonText = ACCEPTED.equals(status) ?
                        pendingTransaction.getRequest().getAccept() : pendingTransaction.getRequest().getReject();
                if (buttonText.charAt(buttonText.length() - 1) == 'y') {
                    buttonText = buttonText.substring(0, buttonText.length() - 2).concat("ied");
                } else if (buttonText.charAt(buttonText.length() - 1) == 'e') {
                    buttonText = buttonText.concat("d");
                } else {
                    buttonText = buttonText.concat("ed");
                }
                return buttonText;
            case EXPIRED:
                return context.getString(R.string.history_details_expired);
            case FAILED_BIOMETRIC:
                return context.getString(R.string.history_details_failed_biometric);
            default:
                return context.getString(R.string.history_details_verified);
        }
    }

    public static String getHistoryText(Context context, TransactionEntity pendingTransaction) {
        if (pendingTransaction.getStatus() == null) {
            return context.getString(R.string.history_details_no_description);
        }
        switch (pendingTransaction.getStatus()) {
            case ACCEPTED:
                return pendingTransaction.getAcceptHistoryMessage();
            case REJECTED:
                return pendingTransaction.getRejectHistoryMessage();
            case EXPIRED:
                return pendingTransaction.getExpiredHistoryMessage();
            case FAILED_BIOMETRIC:
                return pendingTransaction.getFailedBiometricHistoryMessage();
            default:
                return context.getString(R.string.history_details_no_description);
        }
    }
}