package com.covrsecurity.io.utils;

import android.text.TextUtils;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;

import java.util.Map;

public class TransactionTemplateUtils {

    static public String buildTransactionDescription(String descriptionTemplate,
                                                   Map<String, String> params) {
        String description = descriptionTemplate;
        if (!TextUtils.isEmpty(description)) {
            for (String parameterKey : params.keySet()) {
                description = description.replaceAll(AppAdapter.resources().
                                getString(R.string.pending_request_extra_text_template,
                                        parameterKey),
                        params.get(parameterKey));
            }
        }

        return description;
    }
}
