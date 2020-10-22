package com.covrsecurity.io.ui.fragment.authorized.vault;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import androidx.fragment.app.Fragment;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentCovrVaultAbstractBinding;
import com.covrsecurity.io.model.databinding.ToolbarModel;
import com.covrsecurity.io.ui.fragment.BaseChildFragment;

public abstract class CovrVaultBaseChildFragment<ContentBinding extends ViewDataBinding> extends BaseChildFragment<FragmentCovrVaultAbstractBinding> {

    protected static final String SHOW_ANIMATION_ON_START = "SHOW_ANIMATION_ON_START";

    protected ToolbarModel mSecondaryToolbarModel;
    protected Boolean mShowEnterAnimation;
    protected Boolean mShowExitAnimation = true;
    protected ContentBinding mContentBinding;

    @Override
    protected final int getLayoutId() {
        return R.layout.fragment_covr_vault_abstract;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mShowEnterAnimation == null) {// to get arguments only once
            if (getArguments() != null) {
                mShowEnterAnimation = getArguments().getBoolean(SHOW_ANIMATION_ON_START, true);
            } else {
                mShowEnterAnimation = true;
            }
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        Fragment parentFragment = getParentFragment();
        // TODO: 05.06.2017 remove with abstract method and check
        if (parentFragment instanceof CovrVaultToolbarFragment) {
            ((CovrVaultToolbarFragment) parentFragment).hideBottomSheetNoAnim();
        }
        if (mSecondaryToolbarModel == null) {
            mSecondaryToolbarModel = new ToolbarModel.Builder()
                    .setVisibility(showSecondaryToolbar() ? View.VISIBLE : View.GONE)
                    .setLeftImageResId(R.drawable.ic_arrow_back_covr_green_24dp)
                    .setLeftImageClick(v -> onBackPressed())
                    .setTitleText(getSecondaryToolbarTitle())
                    .setBackgroundColor(R.color.covr_vault_light_grey)
                    .setTitleTextColor(R.color.covr_green)
                    .create();
        }
        mBinding.setSecondaryToolbar(mSecondaryToolbarModel);
        mBinding.mainContentStub.setOnInflateListener((stub, inflated) -> {
            mContentBinding = DataBindingUtil.bind(inflated);
            initContentBinding(mContentBinding);
        });
        mBinding.mainContentStub.getViewStub().setLayoutResource(getContentLayoutId());
        if (!mBinding.mainContentStub.isInflated()) {
            mBinding.mainContentStub.getViewStub().inflate();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getParentFragment() instanceof CovrVaultToolbarFragment && ((CovrVaultToolbarFragment) getParentFragment()).isFragmentInBackStack()) {
            mShowEnterAnimation = false;
        }
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0x0) {
            Animator animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
            if (!mShowEnterAnimation && enter) {
                animator.setDuration(0L);
                mShowEnterAnimation = true;
            }
            if (!mShowExitAnimation && !enter) {
                animator.setDuration(0L);
                mShowEnterAnimation = true;
            }
//            animator.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    ((CovrVaultToolbarFragment) getParentFragment()).setSecondaryToolbarTitle(getSecondaryToolbarTitle());
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                }
//            });
            return animator;
        }
        return null;
    }

    protected abstract void initContentBinding(ContentBinding contentBinding);

    @LayoutRes
    protected abstract int getContentLayoutId();

    protected abstract boolean showSecondaryToolbar();

    @StringRes
    protected abstract int getSecondaryToolbarTitle();
}