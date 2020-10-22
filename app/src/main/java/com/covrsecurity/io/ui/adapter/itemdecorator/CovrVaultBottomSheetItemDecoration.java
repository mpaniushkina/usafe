package com.covrsecurity.io.ui.adapter.itemdecorator;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.covrsecurity.io.R;

/**
 * Created by Lenovo on 24.05.2017.
 */

public class CovrVaultBottomSheetItemDecoration extends RecyclerView.ItemDecoration {

    private int mLeft;
    private int mRight;

    public CovrVaultBottomSheetItemDecoration(Context context) {
        mLeft = (int) context.getResources().getDimension(R.dimen.covr_vault_fragment_bottom_sheet_item_left_padding);
        mRight = (int) context.getResources().getDimension(R.dimen.covr_vault_fragment_bottom_sheet_item_right_padding);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        final int itemCount = state.getItemCount();
        if (itemPosition == 0) {
            outRect.left = mLeft * 2;
        } else {
            outRect.left = mLeft;
        }
        if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.right = mRight * 2;
        } else {
            outRect.right = mRight;
        }
    }

}
