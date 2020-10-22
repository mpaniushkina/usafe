package com.covrsecurity.io.ui.fragment.authorized.vault;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.covrsecurity.io.app.CovrApp;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import static com.covrsecurity.io.ui.fragment.authorized.vault.CovrVaultBaseEditFragment.ACTION_NEW_ITEM;

/**
 * Created by Lenovo on 31.05.2017.
 */

public abstract class CovrVaultBaseViewFragment<ContentBinding extends ViewDataBinding, MODEL> extends CovrVaultBaseLoadingFragment<ContentBinding, MODEL> {

    protected static final String RECORD_ID = "RECORD_ID";
    protected static final String WINDOW_MODE = "WINDOW_MODE";

    private final BroadcastReceiver mReturnBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mShouldBeClosed = true;
        }
    };

    protected long mRecordId;
    protected boolean mWindowMode;

    private boolean mShouldBeClosed;

    @Override
    protected boolean showSecondaryToolbar() {
        return !mWindowMode;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle bundle = getArguments();
        mRecordId = bundle.getLong(RECORD_ID);
        mWindowMode = bundle.getBoolean(WINDOW_MODE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReturnBroadcastReceiver, new IntentFilter(ACTION_NEW_ITEM));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShouldBeClosed) {
            onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReturnBroadcastReceiver);
        super.onDestroy();
    }

    @CallSuper
    protected void editAction() {
        if (!mWindowMode) {
            ((CovrVaultToolbarFragment) getParentFragment()).setInBackStack(true);
        }
    }

    protected void openFullScreenFragment(RecordType recordType) {
        CovrVaultToolbarFragment fragment = CovrVaultToolbarFragment.newInstance(recordType, mRecordId);
        replaceFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
    }

    protected void setFavoriteAction(boolean favorite) {
        CovrApp.getInstance().getDatabaseOperationsWrapper().setFavoriteAsync(mRecordId, favorite);
    }

    protected void deleteRecordAndClose() {
        CovrApp.getInstance().getDatabaseOperationsWrapper().deleteAsync(mRecordId, result -> onBackPressed(), true);
    }
}
