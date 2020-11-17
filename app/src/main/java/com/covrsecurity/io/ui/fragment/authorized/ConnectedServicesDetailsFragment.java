package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentConnectionsServicesDetailsBinding;
import com.covrsecurity.io.ui.fragment.BaseFragment;

public class ConnectedServicesDetailsFragment extends BaseFragment<FragmentConnectionsServicesDetailsBinding> {

    private static final String KEY_HEADER = "KEY_HEADER";
    private static final String KEY_BODY = "KEY_BODY";
    private String header;
    private String body;

    public static Fragment newInstance(String header, String body) {
        Fragment fragment = new ConnectedServicesDetailsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_HEADER, header);
        args.putString(KEY_BODY, body);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_connections_services_details;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (getArguments() != null) {
            header = getArguments().getString(KEY_HEADER);
            body = getArguments().getString(KEY_BODY);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.services_details_title);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());
        mBinding.serviceDetailsHeader.setText(header);
        mBinding.serviceDetailsMessage.setText(body);
    }
}
