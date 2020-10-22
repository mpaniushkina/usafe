package com.covrsecurity.io.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.instacart.library.truetime.TrueTime;

/**
 * Created by abaranov on 20.5.16.
 */
public class SlowClickRelativeLayout extends RelativeLayout {
    private OnClickListener mListener;
    private long lastClickTime = 0;
    private final long minDelay = 500;

    public SlowClickRelativeLayout(Context context) {
        super(context);
    }

    public SlowClickRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowClickRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mListener = l;
        super.setOnClickListener(mInnerClickListener);
    }

    private OnClickListener mInnerClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mListener != null) {
                long curTime = TrueTime.now().getTime();
                if (curTime - lastClickTime > minDelay) {
                    lastClickTime = curTime;
                    mListener.onClick(view);
                }
            }
        }
    };

}
