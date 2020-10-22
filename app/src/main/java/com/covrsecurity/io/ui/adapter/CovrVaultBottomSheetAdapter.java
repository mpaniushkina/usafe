package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.model.CovrVaultBottomListItemModel;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultAbstractViewHolder;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultBottomListViewHolder;

import java.util.List;

/**
 * Created by Lenovo on 15.05.2017.
 */

public class CovrVaultBottomSheetAdapter extends CovrVaultAbstractAdapter<CovrVaultBottomListItemModel> {

    public CovrVaultBottomSheetAdapter(List<CovrVaultBottomListItemModel> covrVaultBottomListItemModels, Fragment context,
                                       IOnItemClickListener<CovrVaultBottomListItemModel> listener) {
        super(covrVaultBottomListItemModels, context, listener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_list_covr_vault_bottom;
    }

    @Override
    protected CovrVaultAbstractViewHolder<CovrVaultBottomListItemModel> getViewHolder(View view) {
        return new CovrVaultBottomListViewHolder(view);
    }
}
