package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentConnectedServicesBinding;
import com.covrsecurity.io.ui.adapter.ConnectionsPagerAdapter;
import com.covrsecurity.io.ui.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class ConnectedServicesFragment extends BaseFragment<FragmentConnectedServicesBinding> {

    public static Fragment newInstance() {
        return new ConnectedServicesFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_connected_services;
    }

    private ConnectionsPagerAdapter.ConnectionsPage[] items = {
            new ConnectionsPagerAdapter.ConnectionsPage(1),
            new ConnectionsPagerAdapter.ConnectionsPage(2)
    };

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(R.string.connected_services_title);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> onBackPressed());

        mBinding.infoPager.setAdapter(new ConnectionsPagerAdapter(getChildFragmentManager(), items, R.layout.item_services_pager));
        mBinding.infoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == items.length - 1) {
                    mBinding.buttonNext.setVisibility(View.GONE);
                } else {
                    mBinding.buttonNext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mBinding.buttonNext.setOnClickListener((View chevronRight) -> {
            int current = mBinding.infoPager.getCurrentItem();
            if (current < items.length - 1) {
                mBinding.infoPager.setCurrentItem(current + 1);
            }
        });

    }
}
