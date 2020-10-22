package com.covrsecurity.io.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.CovrApp;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;

import org.joda.time.DateTime;

/**
 * Created by Lenovo on 20.01.2017.
 */

public class EmailUtils {

    public static void sendEmail(String subject, String body, Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        activity.startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    public static String formatForEmail(Context context, TransactionEntity transaction) {
        String historyMessage = null;
        if (StatusEntity.ACCEPTED.equals(transaction.getStatus())) {
            historyMessage = transaction.getAcceptHistoryMessage();
        } else if (StatusEntity.REJECTED.equals(transaction.getStatus())) {
            historyMessage = transaction.getRejectHistoryMessage();
        } else if (StatusEntity.EXPIRED.equals(transaction.getStatus())) {
            historyMessage = transaction.getExpiredHistoryMessage();
        }else if (StatusEntity.FAILED_BIOMETRIC.equals(transaction.getStatus())) {
            historyMessage = transaction.getFailedBiometricHistoryMessage();
        }
        String status = StatusUtils.getStatusText(context, transaction);
        String description = "Description: " + (TextUtils.isEmpty(historyMessage) ?
                CovrApp.getInstance().getString(R.string.history_details_no_description) : historyMessage);

        String createdAt = "";
        if (transaction.getCreated() == 0) {
            DateTime dateTime = new DateTime(transaction.getCreated());
            createdAt = "\n" + "Time: " + DateTimeUtils.getFormattedTime(dateTime);
        }

        return "Company: " + transaction.getCompany().getName() +
                (transaction.getRequest().getTitle() == null ? "" : "\n" + "Verification type: " + transaction.getRequest().getTitle()) +
                "\n" + description +
                createdAt +
                "\n" + "Status: " + status +
                (TextUtils.isEmpty(transaction.getCreatedByIp()) ? "" : "\n" + "Incoming request from: " + transaction.getCreatedByIp()) +
                (TextUtils.isEmpty(transaction.getVerifiedByIp()) ? "" : "\n" + "Verified from: " + transaction.getVerifiedByIp());
    }

    public static String formatForEmail(MerchantEntity merchant) {
        String websiteName = merchant.getCompany().getWebsiteName();
        long createdDate = merchant.getCreatedDate();
        return "Name: " + merchant.getCompany().getName() + "\n" +
                (TextUtils.isEmpty(websiteName) ? "" : "Website: " + websiteName + "\n") +
                (createdDate == 0 ? "" : "Added: " + DateTimeUtils.getFormattedTime(new DateTime(createdDate)) + "\n") +
                "Status: " + merchant.getStatus().getValue();
    }
}