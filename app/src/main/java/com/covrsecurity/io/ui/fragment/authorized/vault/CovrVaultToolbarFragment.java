package com.covrsecurity.io.ui.fragment.authorized.vault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentCovrVaultToolbarBinding;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.model.databinding.ToolbarModel;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.adapter.CovrVaultBottomSheetAdapter;
import com.covrsecurity.io.ui.adapter.IOnItemClickListener;
import com.covrsecurity.io.ui.adapter.itemdecorator.CovrVaultBottomSheetItemDecoration;
import com.covrsecurity.io.ui.adapter.model.CovrVaultBottomListItemModel;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModel;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModelFactory;
import com.covrsecurity.io.utils.FragmentAnimationSet;
import com.covrsecurity.io.utils.RecordTypeCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by bogdan on 4.5.17.
 */
public class CovrVaultToolbarFragment extends CovrVaultBaseParentFragment<FragmentCovrVaultToolbarBinding> {

    private BottomSheetBehavior mBottomSheetBehavior;
    private ToolbarModel mMainToolbarModel;
    private ToolbarModel mSecondaryToolbarModel;

    private RecordType mRecordType;
    private long mRecordId;
    private boolean mIsInBackStack;

    private final IOnItemClickListener<CovrVaultBottomListItemModel> mBottomSheetListener = (item, position) -> {
        mIsInBackStack = true;
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        switch (item.getType()) {
            case NOTE:
                replaceFragment(CovrVaultEditNoteFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_LEFT);
                break;
            case PAYMENT_CARD:
                replaceFragment(CovrVaultEditPaymentCardFragment.newInstance(), null, true, FragmentAnimationSet.SLIDE_LEFT);
                break;
        }
    };

    public static CovrVaultToolbarFragment newInstance() {
        return new CovrVaultToolbarFragment();
    }

    public static CovrVaultToolbarFragment newInstance(RecordType recordType, long recordId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CovrVaultItemsListFragment.RECORD_TYPE, recordType);
        bundle.putLong(CovrVaultBaseViewFragment.RECORD_ID, recordId);
        CovrVaultToolbarFragment fragment = new CovrVaultToolbarFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Inject
    StubViewModelFactory vmFactory;

    @NonNull
    @Override
    protected Class<StubViewModel> getViewModelClass() {
        return StubViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRecordType = (RecordType) bundle.getSerializable(CovrVaultItemsListFragment.RECORD_TYPE);
            mRecordId = bundle.getLong(CovrVaultBaseViewFragment.RECORD_ID);
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mIsInBackStack = false;
        setRetainInstance(true);
        if (mMainToolbarModel == null) {
            mMainToolbarModel = new ToolbarModel.Builder()
                    .setLeftButtonText(R.string.close)
                    .setLeftButtonClick(v -> replaceWithStandingByFragment())
                    .setTitleText(R.string.covr_vault)
//                    .setRightImageResId(R.drawable.ic_add_light)
                    .setRightImageClick(v -> addNewRecord())
                    .create();
        }
        if (mSecondaryToolbarModel == null) {
            mSecondaryToolbarModel = new ToolbarModel.Builder()
//                    .setVisibility(showSecondaryToolbar() ? View.VISIBLE : View.GONE)
                    .setVisibility(View.VISIBLE)
                    .setLeftImageResId(R.drawable.ic_arrow_back_covr_green_24dp)
                    .setLeftImageClick(v -> onBackPressed())
                    .setTitleText(R.string.covr_vault)
                    .setBackgroundColor(R.color.covr_vault_light_grey)
                    .setTitleTextColor(R.color.covr_green)
                    .create();
        }
        mBinding.setMainToolbar(mMainToolbarModel);
        mBinding.setSecondaryToolbar(mSecondaryToolbarModel);
        setUpBottomSheet();
        if (getChildFragmentManager().findFragmentById(R.id.covr_vault_child_container) == null) {
            Fragment mainFragment = CovrVaultMainFragment.newInstance(false);
//            replaceChildFragment(mainFragment, mainFragment.getArguments(), true, 0, 0, 0, 0);
            replaceChildFragment(mainFragment, mainFragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
            if (mRecordType != null) {
                Fragment listFragment = CovrVaultItemsListFragment.newInstance(mRecordType, false);
                replaceChildFragment(listFragment, listFragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);

                Fragment viewFragment;
                if (RecordType.PAYMENT_CARD.equals(mRecordType)) {
                    viewFragment = CovrVaultViewCardFragment.newInstance(mRecordId, false, false);
                } else {
                    viewFragment = CovrVaultViewNoteFragment.newInstance(mRecordId, false, false);
                }
                replaceChildFragment(viewFragment, viewFragment.getArguments(), true, FragmentAnimationSet.SLIDE_LEFT);
            }
        }
    }

    @Override
    public void closeChildFragment() {
        getChildFragmentManager().popBackStack();
    }

    @Override
    public void showChildFragment() {
//        replaceChildFragment(AddPartnershipFragment.newInstance(), null, true, 0, 0, 0, 0);
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.covr_vault_child_container;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_covr_vault_toolbar;
    }

    @Override
    protected int getTitleId() {
        return R.string.covr_vault;
    }

    //DON'T NAME IT isInBackStack()
    public boolean isFragmentInBackStack() {
        return mIsInBackStack;
    }

    public void setInBackStack(boolean isInBackStack) {
        mIsInBackStack = isInBackStack;
    }

    private void setUpBottomSheet() {
        LinearLayout llBottomSheet = mBinding.bottomSheet.bottomSheetContainer;
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        mBinding.bottomSheet.covrVaultBottomList.setHasFixedSize(true);
        mBinding.bottomSheet.covrVaultBottomList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.bottomSheet.covrVaultBottomList.addItemDecoration(new CovrVaultBottomSheetItemDecoration(getActivity()));
        mBinding.bottomSheet.covrVaultBottomList.setAdapter(new CovrVaultBottomSheetAdapter(getBottomContent(), this, mBottomSheetListener));
        mBinding.bottomSheet.covrVaultBottomCloseButton.setOnClickListener(v -> hideBottomSheet());
    }

    private List<CovrVaultBottomListItemModel> getBottomContent() {
        return new ArrayList<CovrVaultBottomListItemModel>() {{
            add(new CovrVaultBottomListItemModel(R.string.covr_vault_bottom_sheet_note, R.drawable.category_icon_note_small, RecordTypeCompat.NOTE));
            add(new CovrVaultBottomListItemModel(R.string.covr_vault_bottom_sheet_payment_card, R.drawable.category_icon_paymentcard_small, RecordTypeCompat.PAYMENT_CARD));
        }};
    }

    public final void replaceWithStandingByFragment() {
        ((AuthorizedActivity) getActivity()).replaceWithStandingByFragment();
    }

    private void addNewRecord() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.appear);
        mBinding.bottomSheetShadow.startAnimation(fadeOut);
        mBinding.bottomSheetShadow.setVisibility(View.VISIBLE);
    }

    public void hideBottomSheet() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);
        mBinding.bottomSheetShadow.startAnimation(fadeOut);
        mBinding.bottomSheetShadow.setVisibility(View.GONE);
    }

    public void hideBottomSheetNoAnim() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBinding.bottomSheetShadow.setVisibility(View.GONE);
    }

    public void setSecondaryToolbarTitle(@StringRes int toolbarTitle) {
        mSecondaryToolbarModel.setTitleText(toolbarTitle);
    }

}
