package com.covrsecurity.io.ui.component;

import android.view.animation.Animation;

/**
 * Created by abaranov on 1.5.16.
 */
public abstract class AnimationEndListner implements Animation.AnimationListener {
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public abstract void onAnimationEnd(Animation animation);

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
