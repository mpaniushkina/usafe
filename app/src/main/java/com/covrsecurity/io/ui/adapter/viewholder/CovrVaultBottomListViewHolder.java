package com.covrsecurity.io.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.IOnItemClickListener;
import com.covrsecurity.io.ui.adapter.model.CovrVaultBottomListItemModel;

/**
 * Created by Lenovo on 10.05.2017.
 */

public class CovrVaultBottomListViewHolder extends CovrVaultAbstractViewHolder<CovrVaultBottomListItemModel> {

    private ImageView mImage;
    private TextView mText;

    public CovrVaultBottomListViewHolder(View itemView) {
        super(itemView);
        mImage = (ImageView) itemView.findViewById(R.id.item_covr_vault_bottom_list_icon);
        mText = (TextView) itemView.findViewById(R.id.item_covr_vault_bottom_list_text);
    }

    @Override
    public void bind(CovrVaultBottomListItemModel item, IOnItemClickListener<CovrVaultBottomListItemModel> onItemClickListener, int position) {
        super.bind(item, onItemClickListener, position);
        mImage.setImageResource(item.getIcon());
        mText.setText(item.getTitle());
    }
}
