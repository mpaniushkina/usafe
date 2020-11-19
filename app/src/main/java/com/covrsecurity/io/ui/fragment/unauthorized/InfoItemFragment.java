package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.InfoPagerAdapter;

public class InfoItemFragment extends Fragment {

    private static final String CURRENT_ITEM = "CURRENT_ITEM";

    public static InfoItemFragment getInstance(int position) {
        InfoItemFragment fragment = new InfoItemFragment();
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
        View rootView = inflater.inflate(R.layout.item_info_personal_code, container, false);
        TextView enterNewCcode = rootView.findViewById(R.id.enter_new_code);
        TextView titleNewCode = rootView.findViewById(R.id.tutorTitle);
        TextView tutorText = rootView.findViewById(R.id.tutor_text);
        enterNewCcode.setVisibility(View.GONE);
        if (currentItem == InfoPagerAdapter.ENTER_PAGE) {
            titleNewCode.setText(R.string.enter_code_title);
            tutorText.setText(R.string.enter_code_subheader);
        } else {
            titleNewCode.setText(R.string.verify_code_title);
            tutorText.setText(R.string.verify_code_subheader);
        }
        enterNewCcode.setOnClickListener(v -> ((CreatePersonalCodeFragment) getParentFragment()).goToEnterPage());
        return rootView;
    }
}