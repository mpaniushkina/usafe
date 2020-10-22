package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment.CURRENT_CODE_PAGE;
import static com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment.NEW_CODE_PAGE;
import static com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment.RE_NEW_CODE_PAGE;

public class ChangeCodeInfoItem extends Fragment {

    private static final String FORMAT_DATE_NORMAL_STR = "dd.MM.yyyy HH:mm:ss";
    private static final DateFormat FORMAT = new SimpleDateFormat(FORMAT_DATE_NORMAL_STR);

    private static final String CURRENT_ITEM = "CURRENT_ITEM";

    private TextView mHeaderText;
    private TextView mRemainingAttemptsText;

    public static ChangeCodeInfoItem newInstance(int position) {
        ChangeCodeInfoItem fragment = new ChangeCodeInfoItem();
        Bundle args = new Bundle();
        args.putInt(CURRENT_ITEM, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        int currentItem = args.getInt(CURRENT_ITEM);
        View rootView = inflater.inflate(R.layout.change_code_pager_item, container, false);
        mHeaderText = rootView.findViewById(R.id.change_code_header);
        mRemainingAttemptsText = rootView.findViewById(R.id.change_code_attempts);
        if (currentItem == CURRENT_CODE_PAGE) {
            mHeaderText.setText(getString(R.string.settings_change_password_enter_current));
            clearRemainingAttempts();
        } else if (currentItem == NEW_CODE_PAGE) {
            mHeaderText.setText(getString(R.string.settings_change_password_enter_new));
            mRemainingAttemptsText.setVisibility(View.GONE);
        } else if (currentItem == RE_NEW_CODE_PAGE) {
            mHeaderText.setText(getString(R.string.settings_change_password_reenter_new));
            mRemainingAttemptsText.setVisibility(View.GONE);
        }
        return rootView;
    }

    public void setRemainingAttempts(int attempts) {
        String string = AppAdapter.resources().getString(R.string.settings_change_password_attempts, attempts);
        setRemainingAttempts(string);
    }

    private void setRemainingAttempts(String string) {
        mRemainingAttemptsText.setVisibility(View.VISIBLE);
        mRemainingAttemptsText.setText(string);
    }

    private void clearRemainingAttempts() {
        mRemainingAttemptsText.setVisibility(View.GONE);
    }
}