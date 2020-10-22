package com.covrsecurity.io.utils;

import com.covrsecurity.io.R;

// To keep fragment transition bundles together
public class FragmentAnimationSet {
    public static FragmentAnimationSet SLIDE_BOTTOM =
            new FragmentAnimationSet(R.animator.bottom_slide_out, R.animator.fade_in, R.animator.fade_out, R.animator.bottom_slide_in);

    public static FragmentAnimationSet SLIDE_UP =
            new FragmentAnimationSet(R.animator.bottom_slide_in, R.animator.fade_out, R.animator.fade_in, R.animator.bottom_slide_out);

    public static FragmentAnimationSet SLIDE_LEFT =
            new FragmentAnimationSet(R.animator.right_slide_in, R.animator.fade_out, R.animator.fade_in, R.animator.right_slide_out);

    public static FragmentAnimationSet SLIDE_TO_LEFT =
            new FragmentAnimationSet(R.animator.right_slide_out, R.animator.right_slide_in, R.animator.right_slide_out, R.animator.right_slide_in);

    public static FragmentAnimationSet FADE_IN =
            new FragmentAnimationSet(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);

    public static FragmentAnimationSet FADE_IN_NO_EXIT =
            new FragmentAnimationSet(R.animator.fade_in, 0, R.animator.fade_in, 0);

    public int enter;
    public int exit;
    public int popEnter;
    public int popExit;

    public FragmentAnimationSet(int enter, int exit, int popEnter, int popExit) {
        this.enter = enter;
        this.exit = exit;
        this.popEnter = popEnter;
        this.popExit = popExit;
    }
}
