package com.covrsecurity.io.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.IOnItemClickListener;
import com.covrsecurity.io.ui.adapter.model.CovrVaultItemsListModel;

/**
 * Created by Lenovo on 17.05.2017.
 */

public class CovrVaultItemsListViewHolder extends CovrVaultAbstractViewHolder<CovrVaultItemsListModel> {

    private ImageView mIcon;
    private TextView mTitle;
    private TextView mDescription;

    public CovrVaultItemsListViewHolder(View itemView) {
        super(itemView);
        mIcon = (ImageView) itemView.findViewById(R.id.covr_vault_items_list_icon);
        mTitle = (TextView) itemView.findViewById(R.id.covr_vault_items_list_icon_title);
        mDescription = (TextView) itemView.findViewById(R.id.covr_vault_items_list_icon_description);
    }

    @Override
    public void bind(CovrVaultItemsListModel item, IOnItemClickListener<CovrVaultItemsListModel> onItemClickListener, int position) {
        super.bind(item, onItemClickListener, position);
        mIcon.setImageResource(item.getIconRes());
        mTitle.setText(item.getTitle());
        mDescription.setText(item.getDescription());
    }
}
