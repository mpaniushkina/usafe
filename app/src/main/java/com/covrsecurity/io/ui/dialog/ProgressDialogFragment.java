package com.covrsecurity.io.ui.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.view.Window;

import com.covrsecurity.io.R;

public class ProgressDialogFragment extends DialogFragment {

    private static ProgressDialogFragment instance;

    public static DialogFragment getInstance() {
        if (instance == null) {
            instance = new ProgressDialogFragment();
        }
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog_application);
        Drawable drawable = new ColorDrawable(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        dialog.getWindow().setBackgroundDrawable(drawable);
        return dialog;
    }
}
