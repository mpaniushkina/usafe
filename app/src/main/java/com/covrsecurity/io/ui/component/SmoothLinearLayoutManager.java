package com.covrsecurity.io.ui.component;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * Created by DellUser on 23.01.2017.
 */

public class SmoothLinearLayoutManager extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 80f;
    private Context mContext;

    public SmoothLinearLayoutManager(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(mContext) {
                    @Override
                    public PointF computeScrollVectorForPosition
                    (int targetPosition) {
                        return SmoothLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel
                    (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH/displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
