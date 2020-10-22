package com.covrsecurity.io.ui.component;

import android.animation.Animator;
import android.content.Context;
import androidx.annotation.CallSuper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.model.TimerInfo;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class TimersLayout extends RelativeLayout {
    private static final long DEF_MOVE_ANIMATION_TIME = 300;
    private static final float APPEAR_ANIMATION_MAX_SCALE = 1.1f;
    private static final float APPEAR_ANIMATION_INTERMEDIATE_SCALE = 0.87f;
    private static final long APPEAR_ANIM_TIME_1 = 500;
    private static final long APPEAR_ANIM_TIME_2 = 300;
    private static final long APPEAR_ANIM_TIME_3 = 300;
    private static final long DISAPPEAR_TIME = 500;
    private static final long START_TIME_ERROR = 100;

    private static final int WORKER_SLEEP_TIME = 100;

    private static final boolean ENABLE_LOGS = false;

    private static final int MAX_TIMERS_COUNT = 4;

    private boolean mInitialized = false;

    private int mWidth;
    private int mHeight;
    private int mMaxElementsCount;
    private int mCenterX;
    private int mMaxTimerWidth;
    private int mTimerHalfWidth;
    private volatile boolean mBusyAnimating = false;
    private TextView mTextCounter;
    private TimersListener mInitListener;
    private Worker mWorker;

    // used to quickly replace removed timers with new ones
    private LinkedList<TimerInfo> mInfoArray;
    private Set<TimerInfo> mRunningTimers = new HashSet<>();
    private ArrayList<View> mViewsArray;
    private LinkedList<ViewActionTask> mTasksQueue = new LinkedList<>();

    public TimersLayout(Context context) {
        super(context);
    }

    public TimersLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimersLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void startWorkerThread() {
        mWorker = new Worker();
        mWorker.startWorkerThread();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mInitialized) {
            init();
        }
    }

    public void init() {
        mWidth = getWidth();
        mHeight = getHeight();
        mMaxElementsCount = MAX_TIMERS_COUNT + 1; // fifth is ".. +N" caption
        mCenterX = getWidth() / 2;
        logD("mCenterX: %d ", getWidth());
        mInfoArray = new LinkedList<>();
        mViewsArray = new ArrayList<>();
        mTasksQueue = new LinkedList<>();
        mBusyAnimating = false;
        mMaxTimerWidth = getWidth() / mMaxElementsCount;
        mTimerHalfWidth = getTimerWidth() / 2;
        mTextCounter = placeTextView();

        mInitialized = true;
        if (mInitListener != null) {
            mInitListener.onViewInitialized(this);
        }

        startWorkerThread();
    }

    public void stop() {
        if (mWorker != null) {
            mWorker.stopWorkerThread();
        }
    }



    /********************************** Public timers management *******************************/
    public void addTimer(final TimerInfo timerInfo) {
        if (mViewsArray == null) {
            init();
        }
        if (mViewsArray.size() < MAX_TIMERS_COUNT) {
            logD("-> queue addTimerTask %s", timerInfo.toString());
            mTasksQueue.add(new AddTimerTask(timerInfo));
        } else {
            //addTimerInfoToQueue(timerInfo);
            logD("-> queue addTimerInfoToQueueTask %s", timerInfo.toString());
            mTasksQueue.add(new AddTimerInfoToQueueTask(timerInfo));
        }
    }

    public void updateTimers(LinkedList<TimerInfo> timers) {
        for (TimerInfo timerInfo : timers) {
            if (!mRunningTimers.contains(timerInfo)) {
                addTimer(timerInfo);
            }
        }
        mRunningTimers.addAll(timers);
    }

    public void setInitialTimers(LinkedList<TimerInfo> timers) {
        mViewsArray.clear();
        mRunningTimers.addAll(timers);
        int N = Math.min(MAX_TIMERS_COUNT, timers.size());
        for (int i = 0; i < N; i++) {
            CircleTimer timerView = createDefaultTimer(timers.pop());
            mViewsArray.add(timerView);
        }
        while (timers.size() > 0) {
            mInfoArray.add(timers.pop());
        }
        if (mInfoArray.size() > 0) {
            showCounter();
        }
        // only showing views after adding info to mInfoArray!!!
        for (int i = 0; i < mViewsArray.size(); i++) {
            CovrCircleTimer timerView = (CovrCircleTimer) mViewsArray.get(i);
            putView(timerView, computeXCoordinate(i), 0, getTimerWidth(), getTimerHeight());
            timerView.setListener(new CircleTimer.TimerAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    removeTimer(timerView);
                }
            });
            timerView.startAnim();
        }
        mInitListener.onInitialTimersStarted();
    }

    public void removeTimer(final CovrCircleTimer timer) {
        logD("-> queue removeTimerTask %s", timer.toString());
        mTasksQueue.add(new RemoveTimerTask(timer));
//        removeTimerView(timer);
    }

    /* *********************** Worker ************************************ */
    private class Worker {
        private final String THREAD_NAME = "TIMERS_LAYOUT_WORKER_THREAD";
        private Thread mThread;

        public Worker() {
            mThread = new Thread(()-> {
                while (! mThread.isInterrupted()) {
                    try {
                        if (! getBusyAnimating()) {
                            checkTasksQueue();
                        }
                        Thread.sleep(WORKER_SLEEP_TIME);
                    } catch (InterruptedException ex) {
                        onInterrupted();
                        return;
                    }
                }
                onInterrupted();
            }, THREAD_NAME);
        }

        private void onInterrupted() {
            logD("xXxXxXxxXxXxXxXxXxX");
            logD("xXxXxXxxXxXxXxXxXxX -------- TimersLayout.Worker Thread interrupted ----------- xXXxXxXXXXXXxxXxXxxxxxXx");
            logD("xXxXxXxxXxXxXxXxXxX");
        }

        public void startWorkerThread() {
            mThread.start();
        }

        private void checkTasksQueue() {
            if (mTasksQueue.size() > 0) {
                ViewActionTask task = mTasksQueue.pop();
                logD("@@@@@ WorkerThread -> DE-queue task: %s", task.toString());
                task.runTask();
            } else if (mInfoArray.size() > 0) {
                logD("@@@@@ WorkerThread: No queued tasks -> call addTimer");
                TimerInfo info = mInfoArray.pop();
                if (info != null) {
                    addTimer(info);
                }
            }
        }

        public void stopWorkerThread() {
            mThread.interrupt();
        }
    }



    /* *********************** Adding / removal tasks ******************** */
    private abstract class ViewActionTask {
        @CallSuper
        public void runTask() {
            onStartTask();
        }

        private final void onStartTask() {
            setBusyAnimating(true);
        }
    }


    //--------------- +
    private class AddTimerTask extends ViewActionTask {
        private TimerInfo mTimerInfo;

        public AddTimerTask(TimerInfo timerInfo) {
            mTimerInfo = timerInfo;
        }

        @Override
        public void runTask() {
            super.runTask();
            logD(">>> > > >> > >> run AddTimerTask: %s", this.toString());
                if (mViewsArray.size() < MAX_TIMERS_COUNT) {
                    logD("-> run addTimerView( %s )", mTimerInfo.toString());
                    ActivityUtils.scheduleOnMainThread(() -> {
                        addTimerView(mTimerInfo);
                    });
                } else {
                    logD("-> queue addTimerInfoToQueueTask %s", mTimerInfo.toString());
                    mTasksQueue.add(new AddTimerInfoToQueueTask(mTimerInfo));
                    finishCurrentTask(); // <<<<<<<<<<<<<<<<<<<< FINISH TASK
                }
        }

        @Override
        public String toString() {
            return String.format("AddTimerTask: %s", mTimerInfo.toString());
        }
    }


    //----------- +i
    private class AddTimerInfoToQueueTask extends ViewActionTask {
        private TimerInfo mTimerInfo;

        public AddTimerInfoToQueueTask(TimerInfo timerInfo) {
            mTimerInfo = timerInfo;
        }

        @Override
        public void runTask() {
            super.runTask();
            logD(">>> > > >> > >> run AddTimerInfoToQueueTask: %s", this.toString());
            ActivityUtils.scheduleOnMainThread(() -> {
                addTimerInfoToQueue(mTimerInfo);
            });
        }

        @Override
        public String toString() {
            return String.format("AddTimerInfoToQueueTask: %s", mTimerInfo.toString());
        }
    }



    //------------ -
    private class RemoveTimerTask extends ViewActionTask {
        private CovrCircleTimer mCircleTimer;

        public RemoveTimerTask(CovrCircleTimer timerInfo) {
            mCircleTimer = timerInfo;
        }

        @Override
        public void runTask() {
            super.runTask();
            logD(">>> > > >> > >> run RemoveTimerTask: %s", this.toString());
            // if view is no longer shown - consider task already finished
            boolean found = false;
            for (View v: mViewsArray) {
                if (v == mCircleTimer) {
                    found = true;
                }
            }
            if (found) {
                ActivityUtils.scheduleOnMainThread(() -> {
                    removeTimerView(mCircleTimer);
                });
            } else {
                finishCurrentTask();
            }
        }

        @Override
        public String toString() {
            return String.format("RemoveTimerTask: %s", mCircleTimer.toString());
        }
    }

    private void finishCurrentTask() {
        logD("finishCurrentTask()");
        setBusyAnimating(false);
    }

    /*************************** Views management ************************************/
//    private void showTimersWithoutAnimation() {
//        for (int i = 0; i < mViewsArray.size(); i++) {
//            putView(mViewsArray.get(i), computeXCoordinate(i), 0, getTimerWidth(), getTimerHeight());
//        }
//    }

    private void putView(View v, int x, int y, int width, int height) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = x;
        params.topMargin = y;
        addView(v, params);
    }

    private void addTimerView(TimerInfo info) {
        CircleTimer timer = createDefaultTimer(info);
        logD("TimersLayout -> addTimerView() = %s", timer.toString());
        mViewsArray.add(timer);
        playNewTimerAnimation();
    }

    private void removeTimerView(CovrCircleTimer timer) {
        logD("TimersLayout -> removeTimerView() = %s", timer.toString());
        mViewsArray.remove(timer);
        mRunningTimers.remove(timer.getTimerInfo());
        invalidateTimersPositions(mViewsArray.size() - 1);
        ActivityUtils.scheduleOnMainThread(() -> {
            disappearTimer(timer, () -> {
                //finishCurrentTask(); // <<<<< FINISH TASK
                removeView(timer);
            });
            mInitListener.onTimerFinished();
        }, DEF_MOVE_ANIMATION_TIME + START_TIME_ERROR);
        ActivityUtils.scheduleOnMainThread(() -> {
            finishCurrentTask(); // <<<<< FINISH TASK
        }, DEF_MOVE_ANIMATION_TIME + DISAPPEAR_TIME + START_TIME_ERROR);
    }

    private void playNewTimerAnimation() {
        final int newViewIndex;
        if (mViewsArray.size() > 1) {
            // animate existing views
            // last view in array is timer
            newViewIndex = mViewsArray.size() - 1;
            invalidateTimersPositions(newViewIndex - 1);
        } else {
            newViewIndex = 0;
        }
        CovrCircleTimer mCircleTimer = (CovrCircleTimer) mViewsArray.get(newViewIndex);
        mCircleTimer.setListener(new CircleTimer.TimerAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                removeTimer(mCircleTimer);
            }
        });
        ActivityUtils.scheduleOnMainThread(() -> {
            appearTimer(mCircleTimer, () -> {
                finishCurrentTask(); // <<<<< FINISH TASK
            });
            mCircleTimer.startAnim();
            putView(mCircleTimer, computeXCoordinate(newViewIndex), 0, getTimerWidth(), getTimerHeight());
        }, DEF_MOVE_ANIMATION_TIME + START_TIME_ERROR);
    }

    private void invalidateTimersPositions(int lastViewIndexToInvalidate) {
        logD("invalidateTimersPositions( %d )", lastViewIndexToInvalidate);
        if (mInfoArray.size() > 0) {
            showCounter();
        } else {
            hideCounter();
        }
        for (int i = 0; i <= lastViewIndexToInvalidate; i++) {
            moveTimerTo((CovrCircleTimer) mViewsArray.get(i), computeXCoordinate(i), 0, null);
        }
    }


    private void addTimerInfoToQueue(TimerInfo info) {
        logD("addTimerInfoToQueue: %s", info.toString());
        // too many timers. Add this one info to array to use it later
        mInfoArray.add(info);
        if (mInfoArray.size() == 1) {
            invalidateTimersPositions(mViewsArray.size() - 1);
            ActivityUtils.scheduleOnMainThread(()->{
                finishCurrentTask(); // <<<<< FINISH TASK
            }, DEF_MOVE_ANIMATION_TIME + START_TIME_ERROR);
        } else {
            showCounter();
            finishCurrentTask(); // <<<<< FINISH TASK
        }
    }

    private TextView placeTextView() {
        TextView t = new TextView(getContext());
        t.setText("... +42");
        t.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        t.setTextColor(getResources().getColor(R.color.soft_green));
        t.setVisibility(View.INVISIBLE);
        putView(t, computeXCoordinate(mMaxElementsCount - 1, mMaxElementsCount), 0, mMaxTimerWidth, getTimerHeight());
        return t;
    }

    public void hideCounter() {
        mTextCounter.setVisibility(GONE);
    }

    public void showCounter() {
        mTextCounter.setText("... +" + mInfoArray.size());
        mTextCounter.setVisibility(VISIBLE);
    }


    /************************* Stuff for computing coordinates and dimensions ***********************/
    protected int getTimerWidth() {
        return Math.min(getHeight(), mMaxTimerWidth);
    }

    protected int getTimerHeight() {
        return Math.min(getHeight(), mMaxTimerWidth);
    }

    private int computeXCoordinate(int viewIndex) {
        int n = mViewsArray.size();
        return computeXCoordinate(viewIndex, n);
    }

    private int computeXCoordinate(int viewIndex, int n) {
        int c = mCenterX;
        int z = mTimerHalfWidth;
        int x = c - (n + 2 - 2 * (viewIndex + 1)) * z;
        if (mInfoArray.size() > 0) {
            // make place for textview
            x -= z;
        }
        return x;
    }

    /************************* Animation actions ***************************/
    private void moveTimerTo(CovrCircleTimer circleTimer, int x, int y, OnTaskEndListener listener) {
        int dx = x - circleTimer.getLeft();
        int dy = y - circleTimer.getTop();
        logD("moveTimerTo( %s : %d, %d )", circleTimer.toString(), dx, dy);
        if (dx != 0 || dy != 0) {
            logD("REMOVE LISTENER --- X ---> %s", circleTimer.toString());
            circleTimer.removeListener();
            Animation appear = createMoveAnimation(dx, dy, DEF_MOVE_ANIMATION_TIME);
            appear.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
//                logD("TimersLayout -> Move timer animation started");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    logD("TimersLayout -> Move timer animation end");
                    circleTimer.cancelAnimation();
                    float v = circleTimer.getValue();
                    logD("timer %s finished move", circleTimer);
                    logD("----");
                    logD("---- is due %b", circleTimer.isDue());
                    logD("----");
                    if (!circleTimer.isDue()) {
                        int remainingTime = Math.round((float) circleTimer.getAnimDuration() * (1 - circleTimer.getPhase()));
                        //CircleTimer timer = createDefaultTimer(v, circleTimer.getMaxValue(), remainingTime);
                        CovrCircleTimer timer = createDefaultTimer(((CovrCircleTimer) circleTimer).getTimerInfo());
                        logD("SET LISTENER --- + ---> %s", timer.toString());
                        timer.setListener(new CircleTimer.TimerAnimationListener() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                removeTimer(timer);
                            }
                        });
                        timer.startAnim();
                        putView(timer, circleTimer.getLeft() + dx, circleTimer.getTop() + dy, getTimerWidth(), getTimerHeight());
                        removeView(circleTimer);
                        logD("timer => timer %s =====> %s", ((CircleTimer) circleTimer).toString(), ((CircleTimer) timer).toString());
                        int index = mViewsArray.indexOf(circleTimer);
                        mViewsArray.remove(circleTimer);
                        if (index != -1) {
                            mViewsArray.add(index, timer);
                        } else {
                            logD("Could add %s to views Array! Idex of old timer not found", timer.toString());
                        }
                    } else {
                        CovrCircleTimer timer = createDefaultTimer(((CovrCircleTimer) circleTimer).getTimerInfo());
                        putView(timer, circleTimer.getLeft() + dx, circleTimer.getTop() + dy, getTimerWidth(), getTimerHeight());
                        removeView(circleTimer);
                        int index = mViewsArray.indexOf(circleTimer);
                        mViewsArray.remove(circleTimer);
                        if (index != -1) {
                            mViewsArray.add(index, timer);
                        }
                        // we have replaced old timer with new one, but it's already overdue. So we have to remove it
                        logD("timer => timer %s =====> %s ((( overdue )))", circleTimer.toString(), timer.toString());
                        removeTimer(timer);
                    }
                    if (listener != null) {
                        listener.onTaskEnd();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    logD("Animation ____REPEAT___ ???");
                }
            });
            circleTimer.setAnimation(appear);
            circleTimer.animate();
        }
    }

    private void appearTimer(CircleTimer timer, OnTaskEndListener listener) {
        if (! ((CovrCircleTimer) timer).isDue()) {
            Animation appear = createAppearAnimation();
            if (listener != null) {
                appear.setAnimationListener(new AnimationEndListner() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        listener.onTaskEnd();
                    }
                });
            }
            timer.setAnimation(appear);
            timer.animate();
        } else {
            listener.onTaskEnd();
        }
    }

    private void disappearTimer(final CircleTimer timer, OnTaskEndListener listener) {
        Animation animation = createDisappearAnimation();
        if (listener != null) {
            animation.setAnimationListener(new AnimationEndListner() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onTaskEnd();
                }
            });
        }
        timer.setAnimation(animation);
        timer.animate();
        removeView(timer);
    }




    /************************* Animations creation ***************************/
    private Animation createMoveAnimation(float dx, float dy, long duration) {
        TranslateAnimation a = new TranslateAnimation(0, dx, 0, dy);
        a.setDuration(duration);
        a.setFillAfter(true);
        return a;
    }

    private Animation createAppearAnimation() {
        final float centerX = 0.5f;
        final float centerY = 0.5f;

        AnimationSet set = new AnimationSet(false);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(APPEAR_ANIM_TIME_1 - ConstantsUtils.THREE_HUNDRED_MILLISECONDS);
        fadeIn.setInterpolator(new AccelerateInterpolator());

        ScaleAnimation rise1 = new ScaleAnimation(0f, APPEAR_ANIMATION_MAX_SCALE, 0f, APPEAR_ANIMATION_MAX_SCALE, ScaleAnimation.RELATIVE_TO_SELF, centerX, ScaleAnimation.RELATIVE_TO_SELF, centerY);
        rise1.setDuration(APPEAR_ANIM_TIME_1);
        rise1.setInterpolator(new LinearInterpolator());

        Animation dive = new ScaleAnimation(APPEAR_ANIMATION_MAX_SCALE, APPEAR_ANIMATION_INTERMEDIATE_SCALE,
                APPEAR_ANIMATION_MAX_SCALE, APPEAR_ANIMATION_INTERMEDIATE_SCALE, ScaleAnimation.RELATIVE_TO_SELF, centerX, ScaleAnimation.RELATIVE_TO_SELF, centerY);
        dive.setStartOffset(APPEAR_ANIM_TIME_1);
        dive.setDuration(APPEAR_ANIM_TIME_2);
        dive.setInterpolator(new AccelerateInterpolator());

        Animation rise2 = new ScaleAnimation(APPEAR_ANIMATION_INTERMEDIATE_SCALE, 1.0f,
                APPEAR_ANIMATION_INTERMEDIATE_SCALE, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, centerX, ScaleAnimation.RELATIVE_TO_SELF, centerY);
        rise2.setStartOffset(APPEAR_ANIM_TIME_1 + APPEAR_ANIM_TIME_2);
        rise2.setDuration(APPEAR_ANIM_TIME_3);
        rise2.setInterpolator(new AccelerateInterpolator());

        set.addAnimation(fadeIn);
        set.addAnimation(rise1);
        set.addAnimation(dive);
        set.addAnimation(rise2);
        set.setFillAfter(true);

        return set;
    }

    private Animation createDisappearAnimation() {
        final float centerX = 0.5f;
        final float centerY = 0.5f;
        AnimationSet set = new AnimationSet(false);

        ScaleAnimation dive = new ScaleAnimation(1f, 0f, 1f, 0f, ScaleAnimation.RELATIVE_TO_SELF, centerX, ScaleAnimation.RELATIVE_TO_SELF, centerY);
        dive.setDuration(DISAPPEAR_TIME);
        dive.setInterpolator(new LinearInterpolator());

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(DISAPPEAR_TIME - 100);
        fadeOut.setInterpolator(new DecelerateInterpolator());

        set.addAnimation(dive);
        set.addAnimation(fadeOut);
        set.setFillAfter(true);
        return set;
    }


    /************************* Creating pre-set timer view objects ***************************/
//    private CircleTimer createDefaultTimer(float startValue, float endValue, long animLength) {
//        CircleTimer mCircleTimer = CovrCircleTimer.newInstance(getContext(), startValue, endValue, animLength);
//        mCircleTimer.setMaxScaleRatio(APPEAR_ANIMATION_MAX_SCALE);
//        return mCircleTimer;
//    }

    private CovrCircleTimer createDefaultTimer(TimerInfo info) {
        CovrCircleTimer mCircleTimer = CovrCircleTimer.newInstance(getContext(), info);
        mCircleTimer.setMaxScaleRatio(APPEAR_ANIMATION_MAX_SCALE);
        return mCircleTimer;
    }


    /****************** ======-~._#* LIFECYCLE MAGIC ***************************/
    private interface OnTaskEndListener {
        void onTaskEnd();
    }

    public interface TimersListener {
        void onViewInitialized(TimersLayout v);
        void onInitialTimersStarted();
        void onTimerFinished();
    }

//
//    private OnTaskEndListener mTaskCompleteListener = new OnTaskEndListener() {
//        @Override
//        public void onTaskEnd() {
//            logD("mTaskCompleteListener -> onTaskEnd()");
//            checkTasksQueue();
//        }
//    };

    private void setBusyAnimating(boolean isBusy) {
        mBusyAnimating = isBusy;
        logD("TimersLayout -> set busy animating = %b", mBusyAnimating);
    }

    private boolean getBusyAnimating() {
        return mBusyAnimating;
    }

    public void setListener(TimersListener mInitListener) {
        this.mInitListener = mInitListener;
    }


    /** ********** logging ********** **/
    private void logD(String msg, Object... args) {
        if (ENABLE_LOGS) {
            Log.d("TimersLayout", String.format(msg, args));
        }
    }


}
