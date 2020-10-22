package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.model.CovrVaultItemsListModel;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultAbstractViewHolder;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultItemsListViewHolder;

import java.util.List;

/**
 * Created by Lenovo on 16.05.2017.
 */

public class CovrVaultListItemsAdapter extends CovrVaultAbstractAdapter<CovrVaultItemsListModel> {

    public CovrVaultListItemsAdapter(List<CovrVaultItemsListModel> covrVaultListItemsModels, Fragment context, IOnItemClickListener<CovrVaultItemsListModel> listener) {
        super(covrVaultListItemsModels, context, listener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_vault_stored_payment;
    }

    @Override
    protected CovrVaultAbstractViewHolder<CovrVaultItemsListModel> getViewHolder(View view) {
        return new CovrVaultItemsListViewHolder(view);
    }
}
