package com.covrsecurity.io.ui.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.adapter.CovrVaultAdapter;
import com.covrsecurity.io.ui.adapter.model.CovrVaultItemModel;

/**
 * Created by Lenovo on 10.05.2017.
 */

public class CovrVaultViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImage;
    private TextView mText;

    public CovrVaultViewHolder(View itemView) {
        super(itemView);
        mImage = (ImageView) itemView.findViewById(R.id.key_vault_item_image);
        mText = (TextView) itemView.findViewById(R.id.key_vault_item_text);
    }

    public void bind(CovrVaultItemModel item, CovrVaultAdapter.OnItemClickListener onItemClickListener, int position) {
        mImage.setImageResource(item.getIcon());
        mText.setText(item.getTitle());
        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item, position));
        }
    }
}
