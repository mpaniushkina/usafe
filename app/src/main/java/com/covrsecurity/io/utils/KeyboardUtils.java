package com.covrsecurity.io.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Lenovo on 11.05.2017.
 */

public final class KeyboardUtils {

    private KeyboardUtils() {
    }

    public static void hideKeyboard(Context activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) activity).getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity, View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    //    public static void showKeyboard1(@Nullable EditText editText, @Nullable Context context) {
//        if (editText == null || context == null) {
//            return;
//        }
//        if (!editText.isFocused()) {
//            editText.requestFocus(View.FOCUS_RIGHT);
//        }
//        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (inputManager != null) {
//            inputManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
//        }
//    }
}
