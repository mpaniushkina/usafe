package com.covrsecurity.io.ui.viewmodel.changecode;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.AssessPinCodeStrengthRequestEntity;
import com.covrsecurity.io.domain.entity.request.ChangePinCodeRequestEntity;
import com.covrsecurity.io.domain.entity.request.ValidatePinCodeRequestEntity;
import com.covrsecurity.io.domain.usecase.registred.ChangePinCodeUseCase;
import com.covrsecurity.io.domain.usecase.registred.ValidatePinCodeUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.AssessPinCodeStrengthUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ChangeCodeViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<Void>> assessPinCodeStrengthLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<Void>> validatePinCodeLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<Void>> changePinCodeLiveData = new MutableLiveData<>();

    private final AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase;
    private final ValidatePinCodeUseCase validatePinCodeUseCase;
    private final ChangePinCodeUseCase changePinCodeUseCase;

    private CompositeDisposable disposables = new CompositeDisposable();

    public ChangeCodeViewModel(AssessPinCodeStrengthUseCase assessPinCodeStrengthUseCase, ChangePinCodeUseCase changePinCodeUseCase, ValidatePinCodeUseCase validatePinCodeUseCase) {
        this.assessPinCodeStrengthUseCase = assessPinCodeStrengthUseCase;
        this.changePinCodeUseCase = changePinCodeUseCase;
        this.validatePinCodeUseCase = validatePinCodeUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }

    public void assessPinCodeStrength(char[] covrCode) {
        Disposable disposable = Single.just(covrCode)
                .map(AssessPinCodeStrengthRequestEntity::new)
                .flatMapCompletable(assessPinCodeStrengthUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> assessPinCodeStrengthLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> assessPinCodeStrengthLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> assessPinCodeStrengthLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }

    public void validatePinCode(char[] oldCovrCode) {
        Disposable disposable = Single.just(new ValidatePinCodeRequestEntity(oldCovrCode))
                .flatMapCompletable(validatePinCodeUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> validatePinCodeLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> validatePinCodeLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> validatePinCodeLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }

    public void changePinCode(Context context, char[] oldCovrCode, char[] newCovrCode, boolean skipWeekPassword) {
        Disposable disposable = Single.just(new ChangePinCodeRequestEntity(context, oldCovrCode, newCovrCode, skipWeekPassword))
                .flatMapCompletable(changePinCodeUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> changePinCodeLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> changePinCodeLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> changePinCodeLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }
}
