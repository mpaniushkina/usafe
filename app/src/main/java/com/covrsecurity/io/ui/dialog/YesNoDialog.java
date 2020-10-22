package com.covrsecurity.io.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.covrsecurity.io.R;

public class YesNoDialog extends DialogFragment {
    private YesNoListener mListener;
    private int mTitle;
    private int mMessage;

    public static YesNoDialog newInstance(int title, int message, YesNoListener listener) {
        YesNoDialog fragment = new YesNoDialog();
        fragment.mTitle = title;
        fragment.mMessage = message;
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(mMessage)
                .setTitle(mTitle)
                .setPositiveButton(R.string.yes, (DialogInterface dialog,int whichButton)-> {
                    mListener.onYesClicked();
                })
                .setNegativeButton(R.string.no, (DialogInterface dialog, int whichButton)-> {
                    mListener.onNoClicked();
                }).create();
    }

    public interface YesNoListener {
        void onYesClicked();
        void onNoClicked();
    }
}
