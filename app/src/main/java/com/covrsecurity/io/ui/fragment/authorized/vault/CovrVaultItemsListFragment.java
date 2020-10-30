package com.covrsecurity.io.ui.fragment.authorized.vault;

import androidx.fragment.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.WorkerThread;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.databinding.FragmentCovrVaultItemsListBinding;
import com.covrsecurity.io.greendao.model.database.CovrVaultDbModel;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.ui.adapter.CovrVaultListItemsAdapter;
import com.covrsecurity.io.ui.adapter.IOnItemClickListener;
import com.covrsecurity.io.ui.adapter.model.CovrVaultItemsListModel;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.CardType;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.RecordTypeCompat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.covrsecurity.io.ui.fragment.authorized.vault.CovrVaultBaseEditFragment.ACTION_NEW_ITEM;

/**
 * Created by Lenovo on 17.05.2017.
 */


public class CovrVaultItemsListFragment extends CovrVaultBaseLoadingFragment<FragmentCovrVaultItemsListBinding, List<CovrVaultItemsListModel>> implements
        IOnItemClickListener<CovrVaultItemsListModel> {

    protected static final String RECORD_TYPE = "RECORD_TYPE";

    private final BroadcastReceiver mReturnBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mShouldBeClosed = true;
        }
    };

    private CovrVaultListItemsAdapter mAdapter;
    private RecordType mRecordType;
    private boolean mIsFirstTime;
    private boolean mShouldBeClosed;

    public static Fragment newInstance(RecordType recordType) {
        return newInstance(recordType, true);
    }

    public static Fragment newInstance(RecordType recordType, boolean showAnimationOnStart) {
        Bundle args = new Bundle();
        args.putSerializable(RECORD_TYPE, recordType);
        args.putBoolean(SHOW_ANIMATION_ON_START, showAnimationOnStart);
        CovrVaultItemsListFragment fragment = new CovrVaultItemsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mRecordType = (RecordType) getArguments().getSerializable(RECORD_TYPE);
    }

    @Override
    protected void initContentBinding(FragmentCovrVaultItemsListBinding itemsListBinding) {
        itemsListBinding.recyclerList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CovrVaultListItemsAdapter(new ArrayList<>(), this, this);
        itemsListBinding.recyclerList.setAdapter(mAdapter);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReturnBroadcastReceiver, new IntentFilter(ACTION_NEW_ITEM));
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_covr_vault_items_list;
    }

    @Override
    protected boolean showSecondaryToolbar() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShouldBeClosed) {
            onBackPressed();
        }
        mIsFirstTime = false;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReturnBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected int getSecondaryToolbarTitle() {
        if (RecordType.NOTE.equals(mRecordType)) {
            return R.string.covr_vault_item_notes;
        } else if (RecordType.PAYMENT_CARD.equals(mRecordType)) {
            return R.string.covr_vault_item_payment_cards;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    @WorkerThread
    protected List<CovrVaultItemsListModel> doLoad() {
        List<CovrVaultDbModel> models = IamApp.getInstance().getDatabaseOperationsWrapper().queryList(mRecordType);
        List<CovrVaultItemsListModel> adapterModels = new ArrayList<>(models.size());
        for (CovrVaultDbModel model : models) {
            if (RecordType.NOTE.equals(mRecordType)) {
                adapterModels.add(new CovrVaultItemsListModel(
                        RecordTypeCompat.NOTE.getIconSmall(),
                        model.getMDescription(),
                        ConstantsUtils.FORMAT_DATE_NORMAL1.format(new Date(model.getMUpdatedAt())),
                        model.getMId()));
            } else if (RecordType.PAYMENT_CARD.equals(mRecordType)) {
                CardType cardType = new Gson().fromJson(model.getMAdditionalPlain(), CardType.class);
                adapterModels.add(new CovrVaultItemsListModel(cardType.getSmallIcon(), model.getMDescription(), getString(cardType.getCardIssuer()), model.getMId()));
            }
        }
        return adapterModels;
    }

    @Override
    protected void onLoadSuccess(List<CovrVaultItemsListModel> content) {
        if (!mIsFirstTime && content.size() == 0) {
            ActivityUtils.scheduleOnMainThread(() -> onBackPressed());
        } else {
            mAdapter.setSource(content);
        }
    }

    @Override
    protected void onLoadError(Exception e) {
    }

    @Override
    public void onItemClick(CovrVaultItemsListModel item, int position) {
        Fragment fragment;
        if (RecordType.NOTE.equals(mRecordType)) {
            fragment = CovrVaultViewNoteFragment.newInstance(item.getDbRecordId(), false);
        } else if (RecordType.PAYMENT_CARD.equals(mRecordType)) {
            fragment = CovrVaultViewCardFragment.newInstance(item.getDbRecordId(), false);
        } else {
            throw new UnsupportedOperationException();
        }
        replaceChildFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
    }
}
