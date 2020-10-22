package com.covrsecurity.io.ui.fragment.authorized.vault;

import android.animation.Animator;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.CovrApp;
import com.covrsecurity.io.databinding.FragmentCovrVaultMainBinding;
import com.covrsecurity.io.greendao.model.database.CovrVaultDbModel;
import com.covrsecurity.io.greendao.model.database.CovrVaultDbModelDao;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.ui.adapter.CovrVaultAdapter;
import com.covrsecurity.io.ui.adapter.itemdecorator.SpacingDecoration;
import com.covrsecurity.io.ui.adapter.model.CovrVaultItemModel;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.RecordTypeAdapterConverter;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by bogdan on 4.5.17.
 */
public class CovrVaultMainFragment extends CovrVaultBaseLoadingFragment<FragmentCovrVaultMainBinding, List<CovrVaultItemModel>> implements
        CovrVaultAdapter.OnItemClickListener {

    private static final String TAG = CovrVaultMainFragment.class.getSimpleName();
    private static final int COLUMNS_COUNT = 3;

    private CovrVaultAdapter mAdapter;
    private boolean mBack;

    public static Fragment newInstance() {
        return newInstance(true);
    }

    public static Fragment newInstance(boolean showAnimationOnStart) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_ANIMATION_ON_START, showAnimationOnStart);
        CovrVaultMainFragment fragment = new CovrVaultMainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initContentBinding(FragmentCovrVaultMainBinding mainBinding) {
        mainBinding.setHasContent(false);
        mainBinding.tvCovrVaultAbout.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString spannableString = new SpannableString(getString(R.string.covr_vault_about_covr_vault_));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                openCovrVaultAboutFragment();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString
                .setSpan(clickableSpan, spannableString.length() - getString(R.string.covr_vault_about_covr_vault_).length(),
                        spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mainBinding.tvCovrVaultAbout.setText(spannableString);
        mainBinding.keyVaultRecycler.setLayoutManager(new GridLayoutManager(getActivity(), COLUMNS_COUNT));
        mAdapter = new CovrVaultAdapter(new ArrayList<>(), this, this);
        mainBinding.keyVaultRecycler.setAdapter(mAdapter);
//        mContentBinding.keyVaultRecycler.addItemDecoration(new CovrVaultItemDecoration(COLUMNS_COUNT, getActivity()));
        mContentBinding.keyVaultRecycler.addItemDecoration(new SpacingDecoration((int) getResources().getDimension(R.dimen.covr_vault_main_pager_adapter_items_margin), 0, true));
        getLoaderManager().initLoader(0, null, this).forceLoad();
        FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(() -> {
            if (getFragmentManager() == null || getFragmentManager().getBackStackEntryCount() == 0) {
                ((CovrVaultToolbarFragment) getParentFragment()).replaceWithStandingByFragment();
            }
        });
        mBack = true;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_covr_vault_main;
    }

    @Override
    protected boolean showSecondaryToolbar() {
        return false;
    }

    @Override
    protected int getSecondaryToolbarTitle() {
        return R.string.covr_vault;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Animator animator = super.onCreateAnimator(transit, enter, nextAnim);
        if (animator != null && !enter && mBack) {
            animator.setDuration(0L);
        }
        return animator;
    }

    @Override
    protected List<CovrVaultItemModel> doLoad() {
        WhereCondition.StringCondition condition = new WhereCondition.StringCondition("1 GROUP BY " + CovrVaultDbModelDao.Properties.MType.columnName);
        List<CovrVaultDbModel> models = CovrApp.getInstance().getDatabaseOperationsWrapper().queryListWithCondition(condition);
        List<CovrVaultItemModel> adapterModels = new ArrayList<>(models.size());
        for (CovrVaultDbModel model : models) {
            adapterModels.add(RecordTypeAdapterConverter.convert(model.getMType()));
        }
        return adapterModels;
    }

    @Override
    protected void onLoadSuccess(List<CovrVaultItemModel> content) {
        boolean hasContent = content.size() != 0;
        mContentBinding.setHasContent(hasContent);
        if (hasContent) {
            mAdapter.setSource(content);
        }
    }

    @Override
    protected void onLoadError(Exception e) {
    }

    @Override
    public void onItemClick(CovrVaultItemModel item, int position) {
        mBack = false;
        Fragment fragment;
        switch (item.getType()) {
            case NOTE:
                fragment = CovrVaultItemsListFragment.newInstance(RecordType.NOTE);
                break;
            case PAYMENT_CARD:
                fragment = CovrVaultItemsListFragment.newInstance(RecordType.PAYMENT_CARD);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        replaceChildFragment(fragment, fragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
    }

    private void openCovrVaultAboutFragment() {
        Timber.tag(TAG).d("openCovrVaultAboutFragment");
        replaceChildFragment(CovrVaultAboutFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_LEFT);
    }
}
