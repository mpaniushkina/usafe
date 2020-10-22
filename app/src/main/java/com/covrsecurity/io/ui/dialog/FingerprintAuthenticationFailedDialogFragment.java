package com.covrsecurity.io.ui.dialog;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.covrsecurity.io.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintAuthenticationFailedDialogFragment extends SafeDismissDialogFragment {

    private static final String DIALOG_FAILED_TAG = "com.covrsecurity.io.ui.dialog.FingerprintAuthenticationFailedDialogFragment.DIALOG_FAILED_TAG";
    private static final String TITLE_EXTRA = "TITLE_EXTRA";
    private static final String CONTENT_EXTRA = "CONTENT_EXTRA";
    private static final String NOTE_EXTRA = "NOTE_EXTRA";

    public static FingerprintAuthenticationFailedDialogFragment getInstance(String title, String content, String note) {
        FingerprintAuthenticationFailedDialogFragment fragment = new FingerprintAuthenticationFailedDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_EXTRA, title);
        args.putString(CONTENT_EXTRA, content);
        args.putString(NOTE_EXTRA, note);
        fragment.setArguments(args);
        return fragment;
    }

    public static FingerprintAuthenticationFailedDialogFragment getInstance() {
        return new FingerprintAuthenticationFailedDialogFragment();
    }

    @Override
    protected String provideTag() {
        return DIALOG_FAILED_TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fingerprint_failed, container, false);
        root.findViewById(R.id.ok_button).setOnClickListener(v -> dismiss());
        TextView title = root.findViewById(R.id.fingerprint_title);
        TextView note = root.findViewById(R.id.fingerprint_note);
        TextView description = root.findViewById(R.id.fingerprint_description);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        note.setTypeface(title.getTypeface(), Typeface.ITALIC);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(TITLE_EXTRA)) {
                setTextOrGone(title, bundle.getString(TITLE_EXTRA));
            }
            if (bundle.containsKey(CONTENT_EXTRA)) {
                setTextOrGone(description, bundle.getString(CONTENT_EXTRA));
            }
            if (bundle.containsKey(NOTE_EXTRA)) {
                setTextOrGone(note, bundle.getString(NOTE_EXTRA));
            }
        }
        return root;
    }

    private void setTextOrGone(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }
}