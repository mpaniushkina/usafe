package com.covrsecurity.io.ui.viewmodel.splash;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.covrsecurity.io.domain.usecase.DelayUseCase;
import com.covrsecurity.io.domain.usecase.InitializeTrueTimeUseCase;
import com.covrsecurity.io.domain.usecase.identity.GetNonceUseCase;
import com.covrsecurity.io.domain.usecase.identity.VerifyUseCase;
import com.covrsecurity.io.domain.usecase.playservices.AttestSafetyNetUseCase;
import com.covrsecurity.io.domain.usecase.playservices.CheckPlayServicesUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.CheckRegistrationUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.GetAppUnlockTimeUseCase;

import javax.inject.Inject;

public class SplashViewModelFactory implements ViewModelProvider.Factory {

    private final CheckPlayServicesUseCase checkPlayServicesUseCase;
    private final GetNonceUseCase getNonceUseCase;
    private final AttestSafetyNetUseCase attestSafetyNetUseCase;
    private final VerifyUseCase verifyUseCase;
    private final InitializeTrueTimeUseCase initializeTrueTimeUseCase;
    private final CheckRegistrationUseCase checkRegistrationUseCase;
    private final GetAppUnlockTimeUseCase getAppUnlockTimeUseCase;
    private final DelayUseCase delayUseCase;

    @Inject
    public SplashViewModelFactory(CheckPlayServicesUseCase checkPlayServicesUseCase,
                                  GetNonceUseCase getNonceUseCase,
                                  AttestSafetyNetUseCase attestSafetyNetUseCase,
                                  VerifyUseCase verifyUseCase,
                                  InitializeTrueTimeUseCase initializeTrueTimeUseCase,
                                  CheckRegistrationUseCase checkRegistrationUseCase,
                                  GetAppUnlockTimeUseCase getAppUnlockTimeUseCase,
                                  DelayUseCase delayUseCase) {
        this.checkPlayServicesUseCase = checkPlayServicesUseCase;
        this.getNonceUseCase = getNonceUseCase;
        this.attestSafetyNetUseCase = attestSafetyNetUseCase;
        this.verifyUseCase = verifyUseCase;
        this.initializeTrueTimeUseCase = initializeTrueTimeUseCase;
        this.checkRegistrationUseCase = checkRegistrationUseCase;
        this.getAppUnlockTimeUseCase = getAppUnlockTimeUseCase;
        this.delayUseCase = delayUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel(
                    checkPlayServicesUseCase,
                    getNonceUseCase,
                    attestSafetyNetUseCase,
                    verifyUseCase,
                    initializeTrueTimeUseCase,
                    checkRegistrationUseCase,
                    getAppUnlockTimeUseCase,
                    delayUseCase);
        } else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
