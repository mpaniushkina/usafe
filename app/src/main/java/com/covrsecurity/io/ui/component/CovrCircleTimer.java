package com.covrsecurity.io.ui.component;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.model.TimerInfo;
import com.instacart.library.truetime.TrueTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class CovrCircleTimer extends CircleTimer {
    // for some reason timer stop too soon?? So add extra duration, no time to figure it out..
    private static final long DURATION_FAULT = 0;

    private static final float ZERO_ANGLE = 270f;

    private TimerInfo mTimerInfo;

    public CovrCircleTimer(Context context) {
        super(context);
    }

    public CovrCircleTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CovrCircleTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        setZeroAngle(ZERO_ANGLE);
        setVisibility(View.VISIBLE);
        setColor(getResources().getColor(R.color.white));
        setBackgroundColor(getResources().getColor(R.color.soft_green));
        setBorderColor(getResources().getColor(R.color.soft_green));
        setBorderThickness(4);
        setClockwise(true);
    }

    public static CovrCircleTimer newInstance(Context context, TimerInfo info) {
        AdaptedTime at = adaptTime(info.getStartTime(), info.getDueTime());
        Timber.d("New timer, to be finished at: %d", info.getDueTime());
        return newInstance(context, at.startValue, at.endValue, at.duration, info);
    }

    private static CovrCircleTimer newInstance(Context context, float startValue, float endValue, long animationDuration, TimerInfo timerInfo) {
        CovrCircleTimer timer = new CovrCircleTimer(context);
        timer.setAnimDuration(animationDuration + DURATION_FAULT);
        timer.showValue(startValue, endValue, false);
        timer.mTimerInfo = timerInfo;
        return timer;
    }

    public boolean isDue() {
        return TrueTime.now().getTime() >= mTimerInfo.getDueTime();
    }

    public void setTimerInfo(TimerInfo info) {
        mTimerInfo = info;
        AdaptedTime at = adaptTime(info.getStartTime(), info.getDueTime());
        setAnimDuration(at.duration);
        showValue(at.startValue, at.endValue, false);
    }

    public TimerInfo getTimerInfo() {
        return mTimerInfo;
    }

    private static AdaptedTime adaptTime(long startTime, long endTime) {
        AdaptedTime adaptedTime = new AdaptedTime();
        long curTime = TrueTime.now().getTime();
        adaptedTime.endValue = endTime - startTime;
        adaptedTime.startValue =  curTime - startTime;
        adaptedTime.duration = endTime - curTime;
        if (adaptedTime.duration < 0) {
            adaptedTime.duration = 0;
        }
        if (adaptedTime.startValue < 0) {
            adaptedTime.startValue = 0;
        }
        return adaptedTime;
    }

    private static class AdaptedTime {
        public long duration;
        public float endValue;
        public float startValue;
    }

    public long getCurrentTime() {
        AdaptedTime adaptedTime = new AdaptedTime();
        long curTime = TrueTime.now().getTime();
        long endTime = mTimerInfo.getDueTime();
        adaptedTime.duration = endTime - curTime;
        return adaptedTime.duration;
    }

    @Override
    protected void onAnimationEnd(Animator animator) {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        Timber.e("Timer start time: " + f.format(new Date(mTimerInfo.getStartTime())));
        Timber.e("Timer due time: " + f.format(new Date(mTimerInfo.getDueTime())));
        Timber.e("Timer current time: " + f.format(new Date()));
        super.onAnimationEnd(animator);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
