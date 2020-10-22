package com.covrsecurity.io.ui.adapter.itemdecorator;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.covrsecurity.io.R;

/**
 * Created by Lenovo on 10.05.2017.
 */

public class CovrVaultItemDecoration extends RecyclerView.ItemDecoration {

    private final int GRID_SIZE;

    private int mLeft;
    private int mTop;
    private int mRight;

    public CovrVaultItemDecoration(int columns, Context context) {
        GRID_SIZE = columns;
        mTop = (int) context.getResources().getDimension(R.dimen.covr_vault_fragment_list_item_top_padding);
        mLeft = (int) context.getResources().getDimension(R.dimen.covr_vault_fragment_list_item_left_padding);
        mRight = (int) context.getResources().getDimension(R.dimen.covr_vault_fragment_list_item_right_padding);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mLeft;
        if (parent.getChildPosition(view) % GRID_SIZE == GRID_SIZE - 1) {
            outRect.right = mRight;
        }
        if (parent.getChildPosition(view) < GRID_SIZE) {
            outRect.top = mTop;
        }
    }
}
