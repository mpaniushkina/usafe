package com.covrsecurity.io.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;

class UnsafeLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final String TAG = UnsafeLifecycleCallbacks.class.getName();

    long mStarted;
    long mStopped;
    long mResumed;
    long mPaused;

    private OnApplicationMinimizedListener mOnMinimizedListener;
    private boolean mApplicationWasMinimized;/*It is used for checking closing ZendesklActivity to show screen with Covr Code*/

    UnsafeLifecycleCallbacks(OnApplicationMinimizedListener onMinimizedListener) {
        this.mOnMinimizedListener = onMinimizedListener;
    }

    public boolean isApplicationWasMinimized() {
        return mApplicationWasMinimized;
    }

    public void setApplicationWasMinimized(boolean mIsApplicationWasMinimized) {
        this.mApplicationWasMinimized = mIsApplicationWasMinimized;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity == null) {
            return;
        }
        Log.d(TAG, "onActivityStarted() called with: activity = [" + activity + "]");
        mStarted++;
        if (!(activity instanceof AuthorizedActivity || activity instanceof UnauthorizedActivity) && mApplicationWasMinimized) {
            activity.finish();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed() called with: activity = [" + activity + "]");
        mResumed++;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused() called with: activity = [" + activity + "]");
        mPaused++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped() called with: activity = [" + activity + "]");
        mStopped++;
        if (mStopped == mStarted) {
            mApplicationWasMinimized = true;
            mOnMinimizedListener.onApplicationMinimized();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated() called with: activity = [" + activity + "], savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState() called with: activity = [" + activity + "], outState = [" + outState + "]");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed() called with: activity = [" + activity + "]");
    }

    interface OnApplicationMinimizedListener {
        void onApplicationMinimized();
    }
}