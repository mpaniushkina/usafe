package com.covrsecurity.io.ui.fragment.authorized;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.ConnectionsAdapter;
import com.covrsecurity.io.ui.fragment.BaseFragment;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import java.util.ArrayList;

public class ConnectionsItemFragment extends BaseFragment implements ConnectionsAdapter.ItemClickListener {

    private static final String DESCRIPTION = "description";
    private static final String LAYOUT = "layout";

    private ConnectionsAdapter adapter;
    private String description;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ConnectionsItemFragment newInstance(int description, int layoutRes) {
        ConnectionsItemFragment fragment = new ConnectionsItemFragment();
        Bundle args = new Bundle();
        args.putInt(DESCRIPTION, description);
        args.putInt(LAYOUT, layoutRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        assert getArguments() != null;
        description = getArguments().getString(DESCRIPTION);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        View rootView = inflater.inflate(args.getInt(LAYOUT), container, false);
        RecyclerView rvConnections = rootView.findViewById(R.id.rvConnections);
        rvConnections.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        ArrayList<String> items = new ArrayList<>();
        items.add("Description");
        items.add("Description");
        items.add("Description");
        items.add("Description");
        items.add("Description");
        items.add("Description");
        items.add("Description");
        items.add("Description");
        items.add("Description");
        adapter = new ConnectionsAdapter(getContext(), items);
        rvConnections.setAdapter(adapter);
        adapter.setClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        Fragment fragment = ConnectedServicesDetailsFragment.newInstance(String.valueOf(position), description);
        replaceFragment(fragment, fragment.getArguments(), true);
    }
}
