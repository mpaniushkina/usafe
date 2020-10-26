package com.covrsecurity.io.ui.fragment.unauthorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;

public class WizardItemFragment extends Fragment {
    private static final String DESCRIPTION1 = "description1";
    private static final String DESCRIPTION2 = "description2";
    private static final String DESCRIPTION3 = "description3";
    private static final String LAYOUT = "layout";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WizardItemFragment newInstance(int description1, int description2,  int description3, int layoutRes) {
        WizardItemFragment fragment = new WizardItemFragment();
        Bundle args = new Bundle();
        args.putInt(DESCRIPTION1, description1);
        args.putInt(DESCRIPTION2, description2);
        args.putInt(DESCRIPTION3, description3);
        args.putInt(LAYOUT, layoutRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        View rootView = inflater.inflate(args.getInt(LAYOUT), container, false);
        TextView description1 = (TextView) rootView.findViewById(R.id.texDescription1);
        description1.setText(getString(args.getInt(DESCRIPTION1)));
//        int desc1TextId = args.getInt(DESCRIPTION1);
//        if (desc1TextId != 0) {
//            description1.setVisibility(View.VISIBLE);
//            description1.setText(getString(args.getInt(DESCRIPTION1)));
//        } else {
//            description1.setVisibility(View.GONE);
//        }
        TextView description2 = (TextView) rootView.findViewById(R.id.texDescription2);
        description2.setText(getString(args.getInt(DESCRIPTION2)));
        TextView description3 = (TextView) rootView.findViewById(R.id.texDescription3);
        description3.setText(getString(args.getInt(DESCRIPTION3)));
        return rootView;
    }
}