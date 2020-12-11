package com.covrsecurity.io.ui.viewmodel.myaccount;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.RegisterRecoveryRequestEntity;
import com.covrsecurity.io.domain.usecase.registred.RegisterBiometricRecoveryUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class MyAccountViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<Void>> registerBiometricRecoveryLiveData = new MutableLiveData<>();

    private final RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase;

    @Nullable
    private Disposable disposable;

    public MyAccountViewModel(RegisterBiometricRecoveryUseCase registerBiometricRecoveryUseCase) {
        this.registerBiometricRecoveryUseCase = registerBiometricRecoveryUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void registerRecoveryRequest(byte[] biometricsBytes, byte[] imageIdCard) {
        disposable = Single.just(new RegisterRecoveryRequestEntity(biometricsBytes, imageIdCard))
                .flatMapCompletable(registerBiometricRecoveryUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> registerBiometricRecoveryLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> registerBiometricRecoveryLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> registerBiometricRecoveryLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }
}
