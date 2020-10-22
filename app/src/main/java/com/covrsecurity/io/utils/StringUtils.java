package com.covrsecurity.io.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.View;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.ui.component.TouchableSpan;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public final static Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static final int DEFAULT_SPLIT = 1024;

    public static CharSequence trimTrailingWhitespace(CharSequence source) {

        if (source == null)
            return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i + 1);
    }

    public static Spannable getClickableSpannable(final String plain, final String spannablePart, String intentAction) {
        return getClickableSpannable(plain, spannablePart, () -> {
            Intent intent = new Intent(intentAction);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppAdapter.context().startActivity(intent);
        });
    }

    public static Spannable getClickableSpannable(@StringRes final int plainStrId, @StringRes final int spannablePartStrId, Runnable action, Context context) {
        return getClickableSpannable(context.getString(plainStrId), context.getString(spannablePartStrId), action, null);
    }

    public static Spannable getClickableSpannable(@StringRes final int plainStrId, @StringRes final int spannablePartStrId, Runnable action, ClickableSpanColors spanColors, Context context) {
        return getClickableSpannable(context.getString(plainStrId), context.getString(spannablePartStrId), action, spanColors);
    }

    public static Spannable getClickableSpannable(final String plain, final String spannablePart, Runnable action) {
        return getClickableSpannable(plain, spannablePart, action, null);
    }

    public static Spannable getClickableSpannable(final String plain, final String spannablePart, Runnable action, ClickableSpanColors spanColors) {
        SpannableString spannable = new SpannableString(plain);
        ClickableSpan clickableSpan;
        if (spanColors != null) {
            clickableSpan = new TouchableSpan(spanColors.getNormalTextColor(), spanColors.getPressedTextColor(), spanColors.getPressedBackgroundColor()) {
                @Override
                public void onClick(View widget) {
                    ActivityUtils.runOnMainThread(action);
                }
            };
        } else {
            clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    ActivityUtils.runOnMainThread(action);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
        }
        int indexOfSpannable = plain.indexOf(spannablePart);
        spannable.setSpan(clickableSpan, indexOfSpannable, indexOfSpannable + spannablePart.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    public static Spannable getBoldSpannable(String text) {
        final SpannableString spannableString = new SpannableString(text);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        spannableString.setSpan(bss, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static String base64UrlEncode(byte[] input) {
        return base64UrlEncode(input, Base64.DEFAULT);
    }

    public static String base64UrlEncode(byte[] input, int flags) {
        return new String(Base64.encode(input, flags), UTF8_CHARSET);
    }

    public static byte[] base64UrlDecode(String base64String) {
        return base64UrlDecode(base64String, Base64.DEFAULT);
    }

    public static byte[] base64UrlDecode(String base64String, int flags) {
        return Base64.decode(base64String, flags);
    }

    public static byte[] base64UrlDecode(byte[] input) {
        return base64UrlDecode(input, Base64.DEFAULT);
    }

    public static byte[] base64UrlDecode(byte[] input, int flags) {
        return Base64.decode(input, flags);
    }

    public static List<String> splitEqually(String text) {
        return splitEqually(text, DEFAULT_SPLIT);
    }

    public static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<>((text.length() + size - 1) / size);
        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public static class ClickableSpanColors {

        public int mPressedBackgroundColor;
        public int mNormalTextColor;
        public int mPressedTextColor;
        private Context mContext = AppAdapter.context();

        public ClickableSpanColors(@ColorRes int normalTextColor, @ColorRes int pressedTextColor, @ColorRes int pressedBackgroundColor) {
            mNormalTextColor = normalTextColor;
            mPressedTextColor = pressedTextColor;
            mPressedBackgroundColor = pressedBackgroundColor;
        }

        @ColorInt
        public int getPressedBackgroundColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mContext.getColor(mPressedBackgroundColor);
            } else {
                return mContext.getResources().getColor(mPressedBackgroundColor);
            }
        }

        @ColorInt
        public int getNormalTextColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mContext.getColor(mNormalTextColor);
            } else {
                return mContext.getResources().getColor(mNormalTextColor);
            }
        }

        @ColorInt
        public int getPressedTextColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mContext.getColor(mPressedTextColor);
            } else {
                return mContext.getResources().getColor(mPressedTextColor);
            }
        }
    }
}
