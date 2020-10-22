package com.covrsecurity.io.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;

/**
 * Created by alex on 13.5.16.
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    private static final int MAX_COUNT = 99;

    private int mRequestsCount;
    private int mPartnershipCount;

    public DrawerAdapter(Context context) {
        super(context, R.layout.drawer_list_item, R.id.title, AppAdapter.resources().getStringArray(R.array.menu_items));
    }

    public void setRequestsCount(int requestsCount) {
        mRequestsCount = requestsCount;
        notifyDataSetChanged();
    }

    public void setPartnershipCount(int partnershipCount) {
        mPartnershipCount = partnershipCount;
        notifyDataSetChanged();
    }

    private String getItemsCountString(int value) {
        if(value > MAX_COUNT) {
            return MAX_COUNT + "+";
        } else {
            return "" + value;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if(position == AuthorizedActivity.HISTORY_ITEM) {
            TextView badge = (TextView) v.findViewById(R.id.badge);
            badge.setText(getItemsCountString(mRequestsCount));
            badge.setVisibility(mRequestsCount > 0 ? View.VISIBLE : View.GONE);
        } else if (position == AuthorizedActivity.PARTNERSHIPS_ITEM) {
            TextView badge = (TextView) v.findViewById(R.id.badge);
            badge.setText(getItemsCountString(mPartnershipCount));
            badge.setVisibility(mPartnershipCount > 0 ? View.VISIBLE : View.GONE);
        }
        return v;
    }
}
