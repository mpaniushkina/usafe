package com.covrsecurity.io.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Egor on 01.09.2016.
 */
public class AnimationUtils {

    public static void showDigitAnimation(TextView view, char value, long duration) {
        view.setText(new char[]{value}, 0, 1);
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);
    }

    public static void hideDigitAnimation(TextView view, long duration) {
        view.animate()
                .alpha(0.0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setText("");
                    }
                });
    }

}
