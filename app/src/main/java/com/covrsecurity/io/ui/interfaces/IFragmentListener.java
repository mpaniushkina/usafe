package com.covrsecurity.io.ui.interfaces;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.event.NoInternetEvent;
import com.covrsecurity.io.utils.FragmentAnimationSet;

public interface IFragmentListener {

    void replaceFragment(Fragment fragment, Bundle bundle, boolean addToBackStack);

    void replaceFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, int containerViewId,
            int showAnimationId, int hideAnimationId);

    void replaceFragment(Fragment fragment, Bundle bundle, boolean addToBackStack, FragmentAnimationSet transitionAnimations);

    void showProgress();

    void hideProgress();

    void onNoInternetReceived(NoInternetEvent event);

    void showErrDlg(Throwable t, DialogInterface.OnClickListener listener);

    void dismissErrDlg();

    void showNoInternetDlg(DialogInterface.OnClickListener listener);

    void showErrToast(Throwable t);

    void showToast(int msgId);

    void showToast(String msg);
}
