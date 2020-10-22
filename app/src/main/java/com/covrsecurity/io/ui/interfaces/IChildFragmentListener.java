package com.covrsecurity.io.ui.interfaces;

import androidx.fragment.app.Fragment;
import android.os.Bundle;

import com.covrsecurity.io.utils.FragmentAnimationSet;

public interface IChildFragmentListener {

    void closeChildFragment();
    void showChildFragment();
    void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, FragmentAnimationSet animations);
    void replaceChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack,
                              int enterAnimationId, int exitAnimationId, int popEnterAnimationId, int popExitAnimationId);
    void addChildFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, FragmentAnimationSet animations);
}
