package com.covrsecurity.io.ui.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.covrsecurity.io.utils.exception.NoDataFoundException;

import java.io.IOException;

/**
 * Created by Lenovo on 16.05.2017.
 */

public class AbstractLoader<T> extends AsyncTaskLoader<ContentWrapper<T>> {

    public static final String TAG = AbstractLoader.class.getSimpleName();

    private ContentWrapper<T> mData;
    private AbstractLoaderInterface<T> mAction;

    public AbstractLoader(Context context, AbstractLoaderInterface<T> action) {
        super(context);
        mAction = action;
    }

    @Override
    public ContentWrapper<T> loadInBackground() {
        T data;
        try {
            data = mAction.action();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return new ContentWrapper<>(e);
        }
        if (data != null) {
            return new ContentWrapper<>(data);
        } else {
            return new ContentWrapper<>(new NoDataFoundException());
        }
    }

    @Override
    public void deliverResult(ContentWrapper<T> data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }
        ContentWrapper<T> oldData = this.mData;
        this.mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(ContentWrapper<T> data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    private void releaseResources(ContentWrapper<T> data) {
    }
}
