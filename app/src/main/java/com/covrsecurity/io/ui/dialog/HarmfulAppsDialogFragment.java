package com.covrsecurity.io.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.covrsecurity.io.R;

public class HarmfulAppsDialogFragment extends DialogFragment {

    private View.OnClickListener mListener;
    private String[] mItems;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public static HarmfulAppsDialogFragment newInstance(String[] items,
                                                        View.OnClickListener listener,
                                                        AdapterView.OnItemClickListener
                                                         onItemClickListener) {
        HarmfulAppsDialogFragment fragment = new HarmfulAppsDialogFragment();
        fragment.mListener = listener;
        fragment.mItems = items;
        fragment.mOnItemClickListener = onItemClickListener;

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.harmful_apps_dialog_fragment, container,
                true);

        setCancelable(false);

        initTextViews(v);
        initListView(v);
        initButton(v);

        return v;
    }

    private void initButton(View v) {
        Button mButton = v.findViewById(R.id.button);
        mButton.setOnClickListener(v1 -> {
            mListener.onClick(v1);
            dismiss();
        });
    }

    private void initListView(View v) {
        ListView mListView = v.findViewById(R.id.dialog_fragment_list);
        Context context = getContext();
        if (context != null) {
            mListView.setAdapter(new ArrayAdapter<>(context, R.layout.item_list_harmful_app, R.id.text,
                    mItems));
            mListView.setOnItemClickListener(mOnItemClickListener);
        }
    }

    private void initTextViews(View v) {
        TextView mTextView = v.findViewById(R.id.before_list_message);
        TextView mTextViewEnding = v.findViewById(R.id.after_list_message);
        if (mItems.length == 1) {
            mTextView.setText(R.string.play_protect_warning_message_singular_intro);
            mTextViewEnding.setText(R.string.play_protect_warning_message_singular_ending);
        } else if (mItems.length > 1) {
            mTextView.setText(R.string.play_protect_warning_message_plural_intro);
            mTextViewEnding.setText(R.string.play_protect_warning_message_plural_ending);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }
}
