package com.covrsecurity.io.ui.viewmodel.partnershipdetaills;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.registred.MarkConnectionAsViewedUseCase;

import javax.inject.Inject;

public class PartnershipDetailsViewModelFactory implements ViewModelProvider.Factory {

    private final MarkConnectionAsViewedUseCase markConnectionAsViewedUseCase;

    @Inject
    public PartnershipDetailsViewModelFactory(MarkConnectionAsViewedUseCase markConnectionAsViewedUseCase) {
        this.markConnectionAsViewedUseCase = markConnectionAsViewedUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PartnershipDetailsViewModel.class)) {
            return (T) new PartnershipDetailsViewModel(
                    markConnectionAsViewedUseCase
            );
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
