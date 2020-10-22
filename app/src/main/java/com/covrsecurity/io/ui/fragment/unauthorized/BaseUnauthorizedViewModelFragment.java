package com.covrsecurity.io.ui.fragment.unauthorized;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

public abstract class BaseUnauthorizedViewModelFragment<Binding extends ViewDataBinding, VM extends BaseViewModel> extends
        BaseUnauthorizedFragment<Binding>
        implements
        HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> childFragmentInjector;

    protected VM viewModel;

    @NonNull
    protected abstract Class<VM> getViewModelClass();

    @NonNull
    protected abstract ViewModelProvider.Factory getViewModelFactory();

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, getViewModelFactory()).get(getViewModelClass());
        getLifecycle().addObserver(viewModel);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return childFragmentInjector;
    }
}
