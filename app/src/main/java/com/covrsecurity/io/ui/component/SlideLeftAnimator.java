package com.covrsecurity.io.ui.component;

import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.covrsecurity.io.utils.ConstantsUtils;

public class SlideLeftAnimator extends DefaultItemAnimator {

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(holder.itemView);
        animation.setDuration(ConstantsUtils.THREE_HUNDRED_MILLISECONDS)
                .translationX(-holder.itemView.getWidth()).alpha(0f).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {

            }

            @Override
            public void onAnimationEnd(View view) {
                animation.setListener(null);
                ViewCompat.setAlpha(view, 1);
                ViewCompat.setTranslationX(view, 0);
                dispatchRemoveFinished(holder);
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
        return false;
    }
}
