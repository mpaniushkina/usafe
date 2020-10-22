
package com.covrsecurity.io.ui.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.CallSuper;

import com.instacart.library.truetime.TrueTime;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * Based on Philipp Jahoda's CircleDisplay library
 * https://github.com/PhilJay/CircleDisplay
 * <p>
 * Simplified version for countdown timer animation without text, inner circle and touch support
 *
 * @author Alexander Baranov
 */
@SuppressLint("NewApi")
public class CircleTimer extends View {

    private static final String LOG_TAG = "CircleDisplay";

    private static final int THREE_SECONDS = 3000;

    private static final float FULL_CIRCLE = 360f;
    private static final float HUNDRED_PERCENT = 100f;

    private static final float TWO = 2f;

    /**
     * startangle of the view
     */
    private float mZeroAngle = 270f;

    /**
     * angle that represents the displayed value
     */
    private float mStartAngle = 0f;

    /**
     * current state of the animation
     */
    private float mPhase = 0f;

    /**
     * the currently displayed value, can be percent or actual value
     */
    private float mStartValue = 0f;

    /**
     * the maximum displayable value, depends on the set value
     */
    private float mMaxValue = 0f;

    /**
     * rect object that represents the bounds of the view, needed for drawing
     * the circle
     */
    private RectF mCircleBox = new RectF();

    private Paint mArcPaint;
    private Paint mBackgroundPaint;
    private Paint mBorderPaint;
    private Integer mExpiredColor = null;
    private boolean mIsClockwise = false;
    private TimerAnimationListener mlistener = null;

    /**
     * object animator for doing the drawing animations
     */
    private ObjectAnimator mDrawAnimator;
    private float mBorderThickness = 0;
    private float mMaxZoomRatio;
    private AtomicBoolean mCancelFlag = new AtomicBoolean(false);

    public CircleTimer(Context context) {
        super(context);
        init();
    }

    public CircleTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @CallSuper
    protected void init() {

        mBoxSetup = false;

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Style.FILL);
        mArcPaint.setColor(Color.rgb(192, 255, 140));

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Style.FILL);
        mBackgroundPaint.setColor(Color.rgb(255, 255, 255));

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Style.STROKE);
        mBorderPaint.setColor(Color.rgb(255, 255, 255));

        mDrawAnimator = ObjectAnimator.ofFloat(this, "phase", mPhase, 1.0f).setDuration(THREE_SECONDS);
        mDrawAnimator.setInterpolator(new LinearInterpolator());
    }

    /**
     * boolean flag that indicates if the box has been setup
     */
    private boolean mBoxSetup = false;

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mCancelFlag.get()) {
            super.onDraw(canvas);

            if (!mBoxSetup) {
                mBoxSetup = true;
                setupBox();
            }

            drawWholeCircle(canvas);

            drawValue(canvas);

            drawBorderCircle(canvas);
        } else {
            mDrawAnimator.end();
            mCancelFlag.set(false);
        }
    }

    private void drawBorderCircle(Canvas c) {
        float r = getRadius();
        c.drawCircle(getWidth() / 2, getHeight() / 2, r, mBorderPaint);
    }

    /**
     * draws the background circle with less alpha
     *
     * @param c
     */
    private void drawWholeCircle(Canvas c) {
        float r = getRadius();
        c.drawCircle(getWidth() / 2, getHeight() / 2, r, mBackgroundPaint);
    }

    /**
     * draws the actual value slice/arc
     *
     * @param c
     */
    private void drawValue(Canvas c) {
        float angle = mStartAngle + (FULL_CIRCLE - mStartAngle) * mPhase;
        if (!mIsClockwise) {
            angle = -angle;
        }
        c.drawArc(mCircleBox, mZeroAngle, angle, true, mArcPaint);
    }

    /**
     * sets up the bounds of the view
     */
    private void setupBox() {

        int width = getWidth();
        int height = getHeight();

        float diameter = getDiameter();

        mCircleBox = new RectF(width / 2 - diameter / 2, height / 2 - diameter / 2, width / 2
                + diameter / 2, height / 2 + diameter / 2);
    }

    /**
     * shows the given value in the circle view
     *
     * @param toShow
     * @param total
     * @param animated
     */
    public void showValue(float toShow, float total, boolean animated) {

        mStartAngle = calcAngle(toShow / total * HUNDRED_PERCENT);
        mStartValue = toShow;
        mMaxValue = total;

        if (animated) {
            startAnim();
        } else {
            mPhase = 1f;
            invalidate();
        }
    }

    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * Returns the currently displayed value from the view. Depending on the
     * used method to show the value, this value can be percent or actual value.
     *
     * @return
     */
    public float getValue() {
        return mStartValue + (mMaxValue - mStartValue) * mPhase;
    }

    public void startAnim() {
        mPhase = 0f;
        mDrawAnimator.addListener(new TimerAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                CircleTimer.this.onAnimationEnd(animator);
            }
        });
        mDrawAnimator.start();
    }

    @CallSuper
    protected void onAnimationEnd(Animator animator) {
        Timber.d("Timer animation end at: %d", TrueTime.now().getTime());
        if (mExpiredColor != null) {
            mBorderPaint.setColor(mExpiredColor);
            mBackgroundPaint.setColor(mExpiredColor);
            mArcPaint.setColor(mExpiredColor);
        }
        if (mlistener != null) {
            Timber.d("Timer animation end ---> CALL listener  (( %s ))", CircleTimer.this.toString());
            mlistener.onAnimationEnd(animator);
        } else {
            Timber.d("Timer animation end ---> listener is null   (( %s ))", CircleTimer.this.toString());
        }
    }

    public void setListener(TimerAnimationListener listener) {
        mlistener = listener;
    }

    public void removeListener() {
        mlistener = null;
    }

    public void cancelAnimation() {
        mCancelFlag.set(true);
    }

    /**
     * set the duration of the drawing animation in milliseconds
     *
     * @param durationmillis
     */
    public void setAnimDuration(long durationmillis) {
        mDrawAnimator.setDuration(durationmillis);
    }

    public int getAnimDuration() {
        return (int) mDrawAnimator.getDuration();
    }

    /**
     * returns the diameter of the drawn circle/arc
     *
     * @return
     */
    public float getDiameter() {
        if (mMaxZoomRatio >= 1) {
            return Math.min(getWidth(), getHeight()) / mMaxZoomRatio - 24 - 2 * mBorderThickness;
        } else {
            return Math.min(getWidth(), getHeight()) - 2 * mBorderThickness;
        }
    }

    /**
     * returns the radius of the drawn circle
     *
     * @return
     */
    public float getRadius() {
        return getDiameter() / TWO;
    }

    /**
     * calculates the needed angle for a given value
     *
     * @param percent
     * @return
     */
    private float calcAngle(float percent) {
        return percent / HUNDRED_PERCENT * FULL_CIRCLE;
    }

    /**
     * set the starting angle for the view
     *
     * @param angle
     */
    public void setZeroAngle(float angle) {
        mZeroAngle = angle;
    }

    public void setClockwise(boolean isClockwise) {
        mIsClockwise = isClockwise;
    }

    /**
     * returns the current animation status of the view
     *
     * @return
     */
    public float getPhase() {
        return mPhase;
    }

    /**
     * DONT USE THIS METHOD
     *
     * @param phase
     */
    public void setPhase(float phase) {
        mPhase = phase;
        invalidate();
    }

    /**
     * set the color of the arc
     *
     * @param color
     */
    public void setColor(int color) {
        mArcPaint.setColor(color);
    }

    /**
     * set background color
     *
     * @param color
     */
    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
    }

    public void setBorderColor(int color) {
        mBorderPaint.setColor(color);
    }

    public void setExpiredColor(int color) {
        mExpiredColor = color;
    }

    public void setBorderThickness(int thicknessInPixels) {
        mBorderThickness = thicknessInPixels;
        mBorderPaint.setStrokeWidth(thicknessInPixels);
        setupBox();
    }

    /**
     * paint representing the value bar
     */
    public static final int PAINT_ARC = 2;

    /**
     * sets the given paint object to be used instead of the original/default
     * one
     *
     * @param which, e.g. CircleDisplay.PAINT_TEXT to set a new text paint
     * @param p
     */
    public void setPaint(int which, Paint p) {

        switch (which) {
            case PAINT_ARC:
                mArcPaint = p;
                break;
        }
    }

    public void setAnimationInterpolator(TimeInterpolator interpolator) {
        mDrawAnimator.setInterpolator(interpolator);
    }

    public PointF getCenter() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    /**
     * Use to add additional inner padding to make room for zoom in animation
     *
     * @param mExtraPaddingRatio maximum scale the view will be zoomed in. Should be more than 1 and less than 2.
     */
    public void setMaxScaleRatio(float mExtraPaddingRatio) {
        this.mMaxZoomRatio = mExtraPaddingRatio;
        setupBox();
    }

    public abstract static class TimerAnimationListener extends AnimatorListenerAdapter {
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(128);
        out.append(getClass().getSimpleName());
        out.append(" {");
        out.append(Integer.toHexString(System.identityHashCode(this)));
        out.append('}');
        return out.toString();
    }
}
