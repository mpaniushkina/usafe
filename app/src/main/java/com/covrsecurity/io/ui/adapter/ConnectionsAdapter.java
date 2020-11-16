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

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ConnectionsViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
//    private ItemClickListener mClickListener;

    public ConnectionsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ConnectionsAdapter.ConnectionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_connections_rv, parent, false);
        return new ConnectionsAdapter.ConnectionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectionsAdapter.ConnectionsViewHolder holder, int position) {
        String connectionNameItem = mData.get(position);
        holder.connectionName.setText(connectionNameItem);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ConnectionsViewHolder extends RecyclerView.ViewHolder // implements View.OnClickListener
    {
        TextView connectionName;

        ConnectionsViewHolder(View itemView) {
            super(itemView);
            connectionName = itemView.findViewById(R.id.connectionName);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
//        }
    }

    public String getItem(int id) {
        return mData.get(id);
    }

//    public void setClickListener(ConnectionsAdapter.ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
