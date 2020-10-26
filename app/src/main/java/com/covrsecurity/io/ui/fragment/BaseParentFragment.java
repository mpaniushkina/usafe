package com.covrsecurity.io.ui.fragment;

import androidx.fragment.app.Fragment;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import com.covrsecurity.io.ui.interfaces.IChildFragmentListener;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import timber.log.Timber;

public abstract class BaseParentFragment<T extends ViewDataBinding, VM extends BaseViewModel> extends
        FromMenuFragment<T, VM>
        implements IChildFragmentListener {

    protected abstract int getFragmentContainerId();

    List<Fragment> mChildFragmentList = new ArrayList<>();

    @Override
    public void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                     FragmentAnimationSet animations) {
        replaceChildFragment(fragment, bundle, addToBackStack,
                animations.enter, animations.exit, animations.popEnter, animations.popExit);
    }

    @Override
    public void addChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, FragmentAnimationSet animations) {
        showReplaceChildFrament(fragment, bundle, addToBackStack, animations.enter, animations.exit, animations.popEnter, animations.popExit, true);
    }

    @Override
    public void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                     int enterAnimationId, int exitAnimationId, int popEnterAnimationId, int popExitAnimationId) {

        showReplaceChildFrament(fragment, bundle, addToBackStack, enterAnimationId, exitAnimationId, popEnterAnimationId, popExitAnimationId, false);
    }

    private void showReplaceChildFrament(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                         int enterAnimationId, int exitAnimationId, int popEnterAnimationId,
                                         int popExitAnimationId, boolean add) {
        String fragmentName = fragment.getClass().getName();
        Timber.d("Replacing fragment with %s", fragmentName);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(fragmentName);
        }
        Fragment f = instantiate(getActivity(), fragmentName, bundle);
        if (enterAnimationId != 0 && popExitAnimationId != 0) {
            ft.setCustomAnimations(enterAnimationId, exitAnimationId, popEnterAnimationId, popExitAnimationId);
        }
        mChildFragmentList.add(f);
        if (add) {
            ft.add(getFragmentContainerId(), f, fragmentName);
        } else {
            ft.replace(getFragmentContainerId(), f, fragmentName);
        }
        try {
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Timber.e(e, "Error replacing fragment");
        }
    }

    protected void removeChildFragments() {
        if (mChildFragmentList.size() > 0) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            for (Fragment activeFragment : mChildFragmentList) {
                ft.remove(activeFragment);
            }
            mChildFragmentList.clear();
            ft.commit();
        }
    }

}
