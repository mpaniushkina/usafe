package com.covrsecurity.io.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public SettingsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public SettingsAdapter.SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_settings, parent, false);
        return new SettingsAdapter.SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.SettingsViewHolder holder, int position) {
        String settingsNameItem = mData.get(position);
        holder.settingsName.setText(settingsNameItem);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView settingsName;

        SettingsViewHolder(View itemView) {
            super(itemView);
            settingsName = itemView.findViewById(R.id.settingsName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public String getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
