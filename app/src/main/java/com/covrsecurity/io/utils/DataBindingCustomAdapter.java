package com.covrsecurity.io.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import androidx.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.model.RegistrationPush;

/**
 * Created by Egor on 31.08.2016.
 */
public class DataBindingCustomAdapter {

    private static Integer mDigitAnimationLength;

    public static int getDigitAnimationLength() {
        if (mDigitAnimationLength == null) {
            mDigitAnimationLength = IamApp.getInstance().getResources().getInteger(R.integer.digit_fade_time);
        }
        return mDigitAnimationLength;
    }

    @SuppressWarnings("unused")
    @BindingAdapter({"digits", "position"})
    public static void setupPinKeyboard(TextView textView, SizedStack<Character> digits, int position) {
        String prevDigit = textView.getText().toString();
        char digit = digits.getSafe(position) == null ? Character.MIN_VALUE : digits.getSafe(position);
        if (TextUtils.isEmpty(prevDigit) && digit == Character.MIN_VALUE) {//first launch
            return;
        } else if (TextUtils.isEmpty(prevDigit) && digit != Character.MIN_VALUE) {
            AnimationUtils.showDigitAnimation(textView, digit, getDigitAnimationLength());
        } else if (!TextUtils.isEmpty(prevDigit) && digit == Character.MIN_VALUE) {
            AnimationUtils.hideDigitAnimation(textView, getDigitAnimationLength());
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter("message")
    public static void setMessageStatus(TextView textView, RegistrationPush message) {
        if (message != null) {
            textView.setText(IamApp.getInstance().getString(R.string.verification_sms_status, message.getStatusMessage()));
            boolean successfulStatus = message.isSuccessfulStatus();
            textView.setTextColor(successfulStatus ? textView.getTextColors().getDefaultColor() : Color.RED);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter("enabled")
    public static void setRetryButtonEnabled(TextView textView, boolean enabled) {
        textView.setClickable(enabled);
        textView.setTextColor(ContextCompat.getColor(IamApp.getInstance(),
                enabled ? R.color.clickable_text : R.color.grey));
    }

    @SuppressWarnings("unused")
    @BindingAdapter("fingerprint")
    public static void setVisisibilityDependingOnFingerprint(View view, char useless) {
        Context context = view.getContext();
        boolean canUseFingerprintScanner = FingerprintUtils.getInstance(context).canUseFingerprintScanner(context);
        view.setVisibility(canUseFingerprintScanner ? View.VISIBLE : View.GONE);
    }

    @SuppressWarnings("unused")
    @BindingAdapter("setTextOrGone")
    public static void setTextOrGone(TextView view, @StringRes int textId) {
        if (textId == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(textId);
            view.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter("setTextOrGone")
    public static void setTextOrGone(TextView view, @Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter("setImageResOrGone")
    public static void setImageResOrGone(ImageView view, @DrawableRes int imageId) {
        if (imageId == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setImageResource(imageId);
            view.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter({"setTransformationMethod", "transform"})
    public static void setTransformationMethod(TextView view, @NonNull TransformationMethod transformation, boolean showValue) {
        if (showValue) {
            view.setTransformationMethod(null);
        } else {
            view.setTransformationMethod(transformation);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter({"background_bind", "background_default"})
    public static void setBackground(View view, @ColorRes int color, @ColorRes int defColor) {
        if (color != 0) {
            view.setBackgroundResource(color);
        } else {
            view.setBackgroundResource(defColor);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter({"textColorBind", "textColorDefault"})
    public static void setTextColor(TextView textView, @ColorRes int color, @ColorRes int defColor) {
        Resources resources = textView.getResources();
        if (color != 0) {
            textView.setTextColor(resources.getColor(color));
        } else {
            textView.setTextColor(resources.getColor(defColor));
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter({"srcBind", "srcDefault"})
    public static void setTextSource(TextView textView, @StringRes int text, @StringRes int defText) {
        if (text != 0) {
            textView.setText(text);
        } else {
            textView.setText(defText);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter({"srcBind", "srcDefault"})
    public static void setTextSource(TextView textView, String text, @StringRes int defText) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        } else {
            textView.setText(defText);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter({"srcBind", "srcDefault"})
    public static void setImageSource(ImageView imageView, @DrawableRes int image, @DrawableRes int defImage) {
        if (image != 0) {
            imageView.setImageResource(image);
        } else {
            imageView.setImageResource(defImage);
        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter("src")
    public static void setNoteImage(ImageView imageView, @DrawableRes int imageRes) {
        imageView.setImageResource(imageRes);
    }

    @SuppressWarnings("unused")
    @BindingAdapter("srcCardType")
    public static void setCardTypeImage(ImageView imageView, @Nullable CardType cardType) {
        if (cardType != null) {
            imageView.setImageResource(cardType.getMediumIcon());
        } else {
            imageView.setImageResource(CardType.UNKNOWN.getMediumIcon());
        }
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("unused")
    @BindingAdapter("underlineColor")
    public static void setUnderlineColor(EditText editText, @ColorRes int color) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            editText.setBackgroundTintList(ColorStateList.valueOf(editText.getResources().getColor(color)));
//        } else {
        Drawable drawable = editText.getBackground();
        drawable.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(editText.getResources().getColor(color), PorterDuff.Mode.SRC));
        editText.setBackground(drawable);
//        }
    }

    @SuppressWarnings("unused")
    @BindingAdapter("el_expanded")
    public static void setExpanded(net.cachapa.expandablelayout.ExpandableLayout layout, Boolean expand) {
        if (expand == null) {
            expand = false;
        }
        layout.setExpanded(expand, true);
    }

    @SuppressWarnings("unused")
    @BindingAdapter("nextEditText")
    public static void setNextView(final EditText editText, final EditText nextEditTex) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                nextEditTex.requestFocus();
                return true;
            }
            return false;
        });
    }
}
