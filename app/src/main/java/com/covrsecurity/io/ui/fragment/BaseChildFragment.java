package com.covrsecurity.io.ui.fragment;

import androidx.fragment.app.Fragment;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;

import com.covrsecurity.io.ui.interfaces.IChildFragmentListener;
import com.covrsecurity.io.utils.FragmentAnimationSet;

public abstract class BaseChildFragment<T extends ViewDataBinding> extends BaseFragment<T> {

    protected void closeChildFragment() {
        assert getParentFragment() != null;
        ((IChildFragmentListener) getParentFragment()).closeChildFragment();
    }

    protected void showChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
            int enterAnimationId, int exitAnimationId, int popEnterAnimationId, int popExitAnimationId) {
        ((IChildFragmentListener) getParentFragment())
                .replaceChildFragment(fragment, bundle, addToBackStack, enterAnimationId, exitAnimationId,
                        popEnterAnimationId, popExitAnimationId);
    }

    protected void showChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, FragmentAnimationSet animations) {
        showChildFragment(fragment, bundle, addToBackStack, animations.enter, animations.exit, animations.popEnter, animations.popExit);
    }

    protected void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                     FragmentAnimationSet animations) {
        ((IChildFragmentListener) getParentFragment()).replaceChildFragment(fragment, bundle, addToBackStack,
                animations.enter, animations.exit, animations.popEnter, animations.popExit);
    }

    protected void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                     int enterAnimationId, int exitAnimationId, int popEnterAnimationId, int popExitAnimationId) {
        ((IChildFragmentListener) getParentFragment()).replaceChildFragment(fragment, bundle, addToBackStack,
                enterAnimationId, exitAnimationId, popEnterAnimationId, popExitAnimationId);
    }

    protected void addChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                                    FragmentAnimationSet animations) {
        ((IChildFragmentListener) getParentFragment()).addChildFragment(fragment, bundle, addToBackStack, animations);
    }

}
