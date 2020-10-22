package com.covrsecurity.io.ui.fragment.unauthorized;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.covrsecurity.io.R;

public class TutorialItemFragment extends Fragment {
    private static final String IMAGE = "image_res";
    private static final String LABEL = "label";
    private static final String TEXT = "text";
    private static final String LAYOUT = "layout";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutorialItemFragment newInstance(int label, int text, int layoutRes, int imageRes) {
        TutorialItemFragment fragment = new TutorialItemFragment();
        Bundle args = new Bundle();
        args.putInt(LABEL, label);
        args.putInt(TEXT, text);
        args.putInt(IMAGE, imageRes);
        args.putInt(LAYOUT, layoutRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        View rootView = inflater.inflate(args.getInt(LAYOUT), container, false);
        TextView label = (TextView) rootView.findViewById(R.id.tutor_label);
        int labelTextId = args.getInt(LABEL);
        if(labelTextId != 0) {
            label.setVisibility(View.VISIBLE);
            label.setText(getString(args.getInt(LABEL)));
        } else {
            label.setVisibility(View.GONE);
        }
        TextView text = (TextView) rootView.findViewById(R.id.tutor_text);
        text.setText(getString(args.getInt(TEXT)));
        ImageView image = (ImageView) rootView.findViewById(R.id.tutor_icon);
        image.setImageResource(args.getInt(IMAGE));
        return rootView;
    }
}
