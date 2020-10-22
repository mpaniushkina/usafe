package com.covrsecurity.io.ui.fragment.unauthorized;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        int subheaderTextId;
        TextView label = rootView.findViewById(R.id.tutor_label);
        TextView enterNewCcode = rootView.findViewById(R.id.enter_new_code);
        if (currentItem == InfoPagerAdapter.ENTER_PAGE) {
            label.setText(R.string.enter_code_header);
            subheaderTextId = R.string.enter_code_subheader;
            enterNewCcode.setVisibility(View.GONE);
        } else {
            label.setText(R.string.verify_code_header);
            subheaderTextId = R.string.verify_code_subheader;
            enterNewCcode.setVisibility(View.VISIBLE);
        }

        enterNewCcode.setOnClickListener(v -> ((CreatePersonalCodeFragment) getParentFragment()).goToEnterPage());

        TextView text = rootView.findViewById(R.id.tutor_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString spannableString = new SpannableString(getString(subheaderTextId));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (currentItem == InfoPagerAdapter.ENTER_PAGE) {
                    ((CreatePersonalCodeFragment) getParentFragment()).showReadMoreFragment(getString(R.string.cfg_setup_read_more_personal_code));
                } else {
                    ((CreatePersonalCodeFragment) getParentFragment()).showReadMoreFragment(getString(R.string.cfg_setup_read_more_personal_code_verify));
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString
                .setSpan(clickableSpan, spannableString.length() - getString(R.string.read_more).length(),
                        spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(spannableString);
        return rootView;
    }
}