package com.covrsecurity.io.ui.adapter.viewholder;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.covrsecurity.io.ui.adapter.IOnItemClickListener;
import com.covrsecurity.io.ui.adapter.model.CovrVaultAbstractItemModel;

/**
 * Created by Lenovo on 10.05.2017.
 */

public abstract class CovrVaultAbstractViewHolder<T extends CovrVaultAbstractItemModel> extends RecyclerView.ViewHolder {

    public CovrVaultAbstractViewHolder(View itemView) {
        super(itemView);
    }

    @CallSuper
    public void bind(T item, IOnItemClickListener<T> onItemClickListener, int position) {
        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item, position));
        }
    }

}
