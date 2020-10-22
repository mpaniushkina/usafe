package com.covrsecurity.io.utils;

import androidx.databinding.BindingAdapter;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import android.text.method.MovementMethod;
import android.widget.TextView;

import com.covrsecurity.io.R;

/**
 * Created by Lenovo on 29.05.2017.
 */

public class DataBindingTextView {


    @BindingAdapter("setMovementMethod")
    public static void setExpanded(TextView textView, MovementMethod movement) {
        textView.setMovementMethod(movement);
    }

    @BindingAdapter({"plainStrId", "spannablePartStrId", "action"})
    public static void setClickableSpannable(TextView textView, @StringRes int plainStrId, @StringRes int spannablePartStrId, Runnable action) {
        StringUtils.ClickableSpanColors spanColors = new StringUtils.ClickableSpanColors(R.color.covr_green, R.color.soft_green, R.color.transparent);
        textView.setText(StringUtils.getClickableSpannable(plainStrId, spannablePartStrId, action, spanColors, textView.getContext()));
    }

    @BindingAdapter({"pluralText", "quantity"})
    public static void setQuantityText(TextView textView, @PluralsRes int pluralText, int quantity) {
        String quantityString = textView.getContext().getResources().getQuantityString(pluralText, quantity);
        textView.setText(quantityString);
    }
}
