package com.covrsecurity.io.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.dialog.HarmfulAppsDialogFragment;

public final class DialogUtils {

    public static final String TAG_SECURITY_WARNING_FRAGMENT = "WarningFragment";

    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    public static AlertDialog showOkDialog(Activity context, String title, String message, boolean cancelable) {
        return showOkDialog(context, title, message, context.getString(R.string.ok), null, cancelable);
    }

    public static AlertDialog showOkDialog(Activity context, String message, boolean cancelable) {
        return showOkDialog(context, "", message, context.getString(R.string.ok), null, cancelable);
    }

    public static AlertDialog showOkDialog(Activity context, String message, boolean cancelable, DialogInterface.OnClickListener onClickPositive) {
        return showOkDialog(context, "", message, context.getString(R.string.ok), onClickPositive, cancelable);
    }

    public static AlertDialog showOkDialog(Activity context, String title, String message,
                                           String positiveText, DialogInterface.OnClickListener onClickPositive,
                                           boolean cancelable) {
        if (context != null && !((Activity) context).isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(context).create();
            if (!TextUtils.isEmpty(title)) {
                dialog.setTitle(title);
            }
            if (!TextUtils.isEmpty(message)) {
                dialog.setMessage(message);
            }
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    !TextUtils.isEmpty(positiveText) ? positiveText : context.getString(R.string.ok), onClickPositive);
            dialog.setCancelable(cancelable);
            dialog.show();
            return dialog;
        }
        return null;
    }

    public static AlertDialog showAlertDialog(Activity context, @StringRes int titleResId, @StringRes int messageResId,
                                              @StringRes int positiveTextResId, DialogInterface.OnClickListener onClickPositive,
                                              @StringRes int negativeTextResId, DialogInterface.OnClickListener onClickNegative,
                                              boolean cancelable, boolean titleBold) {
        return showAlertDialog(context, context.getString(titleResId), context.getString(messageResId),
                context.getString(positiveTextResId), onClickPositive, context.getString(negativeTextResId), onClickNegative, cancelable, titleBold);
    }

    public static AlertDialog showAlertDialog(Activity context, String title, String message,
                                              String positiveText, DialogInterface.OnClickListener onClickPositive,
                                              String negativeText, DialogInterface.OnClickListener onClickNegative,
                                              boolean cancelable) {
        return showAlertDialog(context, title, message, positiveText, onClickPositive, negativeText, onClickNegative, cancelable, false);
    }

    public static AlertDialog showAlertDialog(Activity context, String title, String message,
                                              String positiveText, DialogInterface.OnClickListener onClickPositive,
                                              String negativeText, DialogInterface.OnClickListener onClickNegative,
                                              boolean cancelable, boolean titleBold) {
        if (context != null && !context.isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(context).create();
            if (!TextUtils.isEmpty(title)) {
                if (titleBold) {
                    dialog.setTitle(StringUtils.getBoldSpannable(title));
                } else {
                    dialog.setTitle(title);
                }
            }
            if (!TextUtils.isEmpty(message)) {
                dialog.setMessage(message);
            }
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    !TextUtils.isEmpty(positiveText) ? positiveText : context.getString(R.string.yes), onClickPositive);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    !TextUtils.isEmpty(negativeText) ? negativeText : context.getString(R.string.no), onClickNegative);
            dialog.setCancelable(cancelable);
            dialog.show();
            return dialog;
        }
        return null;
    }

    static void showHarmfulAppsDialogFragment(AppCompatActivity activity,
                                              String[] items,
                                              View.OnClickListener okButtonListener,
                                              AdapterView.OnItemClickListener listItemListener) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(TAG_SECURITY_WARNING_FRAGMENT) == null) {
            HarmfulAppsDialogFragment harmfulAppsDialogFragment =
                    HarmfulAppsDialogFragment.newInstance(items, okButtonListener,
                            listItemListener);
            harmfulAppsDialogFragment.show(fragmentManager, TAG_SECURITY_WARNING_FRAGMENT);
        }
    }

    public static void showPromptDialog(Activity context, String title, String message,
                                        String positiveText, PromptDialogListener onClickPositive, String negativeText,
                                        boolean cancelable) {
        if (context != null && !((Activity) context).isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(context).create();
            if (!TextUtils.isEmpty(title)) {
                dialog.setTitle(title);
            }
            if (!TextUtils.isEmpty(message)) {
                dialog.setMessage(message);
            }
            // Set up the input
            final EditText input = new EditText(context);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            dialog.setView(input);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    !TextUtils.isEmpty(positiveText) ? positiveText : context.getString(R.string.ok), (dialog1, which) -> {
                        onClickPositive.onClick(dialog1, which, input);
                    });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    !TextUtils.isEmpty(negativeText) ? negativeText : context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(cancelable);
            dialog.show();
        }
    }

    public interface PromptDialogListener {
        void onClick(DialogInterface dialog, int which, EditText inputView);
    }

}
