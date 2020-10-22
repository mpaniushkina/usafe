package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.model.CovrVaultItemModel;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultViewHolder;

import java.util.List;

/**
 * Created by Lenovo on 09.05.2017.
 */

public class CovrVaultAdapter extends RecyclerView.Adapter<CovrVaultViewHolder> {

    private List<CovrVaultItemModel> mItems;
    private Fragment mContext;
    private OnItemClickListener mOnItemClickListener;

    public CovrVaultAdapter(List<CovrVaultItemModel> items, Fragment context, OnItemClickListener onItemClickListener) {
        mItems = items;
        mContext = context;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public CovrVaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext.getActivity()).inflate(R.layout.item_covr_vault, parent, false);
        return new CovrVaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CovrVaultViewHolder holder, int position) {
        holder.bind(mItems.get(position), mOnItemClickListener, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setSource(List<CovrVaultItemModel> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(CovrVaultItemModel item, int position);
    }
}
