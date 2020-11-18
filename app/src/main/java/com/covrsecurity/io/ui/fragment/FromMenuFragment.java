package com.covrsecurity.io.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.ViewDataBinding;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

/**
 * Created by elena on 5/2/16.
 */
public abstract class FromMenuFragment<Binding extends ViewDataBinding, VM extends BaseViewModel> extends
        BaseViewModelFragment<Binding, VM> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (v != null) {
            TextView title = (TextView) v.findViewById(R.id.title);
            if (title != null) {
                title.setText(getTitleId());
            }
            View leftButton = v.findViewById(R.id.tool_left_button);
            if (leftButton != null) {
                leftButton.setOnClickListener(v1 -> onLeftToolbarButtonClicked());
            }
        }
        return v;
    }

    protected void onLeftToolbarButtonClicked() {
        if (showDrawerOnBack()) {
//            ((AuthorizedActivity) getActivity()).openDrawer();
        }
        getActivity().onBackPressed();
    }

    protected abstract int getTitleId();

    protected boolean showDrawerOnBack() {
        return false;
    }
}
