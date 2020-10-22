package com.covrsecurity.io.ui.fragment.authorized.vault;

import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.WorkerThread;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.util.Log;

import com.covrsecurity.io.ui.loaders.ContentWrapper;

import java.util.Objects;

public abstract class CovrVaultBaseLoadingFragment<ContentBinding extends ViewDataBinding, MODEL> extends CovrVaultBaseChildFragment<ContentBinding> implements
        LoaderManager.LoaderCallbacks<ContentWrapper<MODEL>> {

    public static final String TAG = CovrVaultBaseLoadingFragment.class.getSimpleName();

    @Override
    public Loader<ContentWrapper<MODEL>> onCreateLoader(int id, Bundle args) {
        return new Loader<ContentWrapper<MODEL>>(Objects.requireNonNull(getActivity()));
    }

    @Override
    public void onLoadFinished(Loader<ContentWrapper<MODEL>> loader, ContentWrapper<MODEL> data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        if (data.getException() == null && data.getContent() != null) {
            onLoadSuccess(data.getContent());
        } else {
            onLoadError(data.getException());
        }
    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<MODEL>> loader) {
    }

    @WorkerThread
    protected abstract MODEL doLoad();

    protected abstract void onLoadSuccess(MODEL content);

    protected abstract void onLoadError(Exception e);
}
