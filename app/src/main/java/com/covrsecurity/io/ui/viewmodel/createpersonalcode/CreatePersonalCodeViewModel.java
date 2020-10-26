package com.covrsecurity.io.ui.viewmodel.createpersonalcode;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.AssessPinCodeStrengthRequestEntity;
import com.covrsecurity.io.domain.entity.request.RegisterRequestEntity;
import com.covrsecurity.io.domain.entity.request.SetUpPasswordRequestEntity;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.domain.usecase.unregistered.AssessPinCodeStrengthUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.RegisterUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.SetUpPasswordUseCase;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class CreatePersonalCodeViewModel extends BaseViewModel {

    private final MutableLiveData<BaseState<Void>> assessPinCodeStrengthMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<BaseState<Void>> setUpPasswordMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<BaseState<Void>> registerMutableLiveData = new MutableLiveData<>();

    public final LiveData<BaseState<Void>> assessPinCodeStrengthLiveData = assessPinCodeStrengthMutableLiveData;
    public final LiveData<BaseState<Void>> setUpPasswordLiveData = setUpPasswordMutableLiveData;
    public final LiveData<BaseState<Void>> registerLiveData = registerMutableLiveData;

    private final com.covrsecurity.io.domain.usecase.unregistered.AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase;
    private final com.covrsecurity.io.domain.usecase.unregistered.SetUpPasswordUseCase setUpPasswordUseCase;
    private final com.covrsecurity.io.domain.usecase.unregistered.RegisterUseCase registerUseCase;

    @Nullable
    private Disposable disposable;

    public CreatePersonalCodeViewModel(AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase, SetUpPasswordUseCase setUpPasswordUseCase, RegisterUseCase registerUseCase) {
        this.assessPinCodeStrengthUseCase = assessPinCodeStrengthUseCase;
        this.setUpPasswordUseCase = setUpPasswordUseCase;
        this.registerUseCase = registerUseCase;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void assessPinCodeStrength(char[] covrCode) {
        disposable = Single.just(covrCode)
                .map(AssessPinCodeStrengthRequestEntity::new)
                .flatMapCompletable(assessPinCodeStrengthUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> assessPinCodeStrengthMutableLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> assessPinCodeStrengthMutableLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> assessPinCodeStrengthMutableLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }

    public void setUpPassword(char[] covrCode, boolean skipWeekPassword) {
        disposable = Single.just((new SetUpPasswordRequestEntity(covrCode, skipWeekPassword)))
                .flatMapCompletable(setUpPasswordUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> setUpPasswordMutableLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> setUpPasswordMutableLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> setUpPasswordMutableLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }

    public void register(char[] covrCode, boolean skipWeekPassword) {
        disposable = Single.just(new RegisterRequestEntity(covrCode, skipWeekPassword, null, null))
                .flatMapCompletable(registerUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> registerMutableLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> registerMutableLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> registerMutableLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }
}
