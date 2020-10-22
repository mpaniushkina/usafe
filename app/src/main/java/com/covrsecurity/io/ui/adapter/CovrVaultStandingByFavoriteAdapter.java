package com.covrsecurity.io.ui.adapter;

import androidx.fragment.app.Fragment;
import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.model.CovrVaultFavoriteModel;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultAbstractViewHolder;
import com.covrsecurity.io.ui.adapter.viewholder.CovrVaultFavotiteViewHolder;

import java.util.List;

/**
 * Created by Lenovo on 22.05.2017.
 */

public class CovrVaultStandingByFavoriteAdapter extends CovrVaultAbstractAdapter<CovrVaultFavoriteModel> {

    public CovrVaultStandingByFavoriteAdapter(List<CovrVaultFavoriteModel> covrVaultItemsListModels, Fragment context, IOnItemClickListener<CovrVaultFavoriteModel> listener) {
        super(covrVaultItemsListModels, context, listener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_list_covr_vault_favorite;
    }

    @Override
    protected CovrVaultAbstractViewHolder<CovrVaultFavoriteModel> getViewHolder(View view) {
        return new CovrVaultFavotiteViewHolder(view);
    }
}
