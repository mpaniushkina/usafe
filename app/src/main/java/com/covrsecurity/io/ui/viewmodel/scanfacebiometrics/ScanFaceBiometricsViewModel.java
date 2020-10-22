package com.covrsecurity.io.ui.viewmodel.scanfacebiometrics;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.RecoverRequestEntity;
import com.covrsecurity.io.domain.entity.request.RegisterRequestEntity;
import com.covrsecurity.io.domain.usecase.unregistered.RecoverUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.RegisterUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ScanFaceBiometricsViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<Void>> registerLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<Void>> recoverLiveData = new MutableLiveData<>();

    private final RegisterUseCase registerUseCase;
    private final RecoverUseCase recoverUseCase;

    @Nullable
    private CompositeDisposable disposables = new CompositeDisposable();

    public ScanFaceBiometricsViewModel(RegisterUseCase registerUseCase, RecoverUseCase recoverUseCase) {
        this.registerUseCase = registerUseCase;
        this.recoverUseCase = recoverUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
    }

    public void register(char[] covrCode, boolean skipWeekPassword, byte[] biometricsData, byte[] imageIdCard) {
        final Disposable disposable = Single.just(new RegisterRequestEntity(covrCode, skipWeekPassword, biometricsData, imageIdCard))
                .flatMapCompletable(registerUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> registerLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> registerLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> registerLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        if (disposables == null) {
            disposables = new CompositeDisposable();
        }
        disposables.add(disposable);
    }

    public void recover(String recoverId, byte[] biometricsData) {
        final Disposable disposable = recoverUseCase.execute(new RecoverRequestEntity(recoverId, biometricsData))
                .doOnSubscribe(
                        disposable1 -> recoverLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        () -> recoverLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> recoverLiveData.setValue(BaseState.getErrorInstance(throwable))
                );

        if (disposables == null) {
            disposables = new CompositeDisposable();
        }
        disposables.add(disposable);
    }
}
