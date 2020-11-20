package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentMyAccountBinding;
import com.covrsecurity.io.ui.adapter.SettingsAdapter;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class MyAccountFragment extends BaseFragment<FragmentMyAccountBinding> implements SettingsAdapter.ItemClickListener {

    public static final int CONNECTED_SERVICES_ITEM = 0;
    public static final int HISTORY_ITEM = 1;
    public static final int CHANGE_PIN_CODE_ITEM = 2;
    public static final int RECOVERY_ITEM = 3;
    public static final int HELP_ITEM = 4;

    private SettingsAdapter adapter;

    public static Fragment newInstance() {
        return new MyAccountFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_account;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.toolBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.my_account);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());

        ArrayList<String> settingName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.my_account_names)));
        mBinding.myAccountRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SettingsAdapter(getContext(), settingName);
        adapter.setClickListener(this);
        mBinding.myAccountRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Fragment fragment = null;
        switch (position) {
            case CONNECTED_SERVICES_ITEM:
                fragment = PartnershipFragment.newInstance();
                break;
            case HISTORY_ITEM:
                fragment = HistoryFragment.newInstance();
                break;
            case CHANGE_PIN_CODE_ITEM:
                fragment = ChangeCodeFragment.newInstance(false);
                break;
            case RECOVERY_ITEM:
                fragment = HelpFragment.newInstance();
                break;
//            case HELP_ITEM:
//                fragment = HelpFragment.newInstance();
//                break;
        }
        replaceFragment(fragment, fragment.getArguments(), true);
    }
}
