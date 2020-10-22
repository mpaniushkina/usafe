package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.covrsecurity.io.ui.adapter.model.CovrVaultAbstractItemModel;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultAbstractViewHolder;

import java.util.List;

/**
 * Created by Lenovo on 15.05.2017.
 */

public abstract class CovrVaultAbstractAdapter<MODEL extends CovrVaultAbstractItemModel> extends RecyclerView.Adapter<CovrVaultAbstractViewHolder<MODEL>> {

    private List<MODEL> mModels;
    private Fragment mContext;
    private IOnItemClickListener<MODEL> mListener;

    CovrVaultAbstractAdapter(List<MODEL> models, Fragment context,  IOnItemClickListener<MODEL> listener) {
        mModels = models;
        mContext = context;
        mListener = listener;
    }

    @Override
    public CovrVaultAbstractViewHolder<MODEL> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext.getActivity()).inflate(getItemLayoutId(), parent, false);
        return getViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CovrVaultAbstractViewHolder<MODEL> holder, int position) {
        holder.bind(mModels.get(position), mListener, position);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void setSource(List<MODEL> items) {
        mModels.clear();
        mModels.addAll(items);
        notifyDataSetChanged();
    }

    protected abstract int getItemLayoutId();

    protected abstract CovrVaultAbstractViewHolder<MODEL> getViewHolder(View view);
}
