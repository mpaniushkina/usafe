package com.covrsecurity.io.ui.viewmodel.unauthorizedactivity;

import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.usecase.identity.IsApprovedUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.ClearUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.baseactivity.BaseActivityViewModel;

import io.reactivex.disposables.Disposable;

public class UnauthorizedActivityViewModel extends BaseActivityViewModel {

    public final MutableLiveData<BaseState<Void>> clearDataLiveData = new MutableLiveData<>();

    private final ClearUseCase clearUseCase;

    public UnauthorizedActivityViewModel(IsApprovedUseCase isApprovedUseCase, ClearUseCase clearUseCase) {
        super(isApprovedUseCase);
        this.clearUseCase = clearUseCase;
    }

    public void clearData() {
        Disposable disposable = clearUseCase.execute(null)
                .subscribe(
                        () -> clearDataLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> clearDataLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        compositeDisposable.add(disposable);
    }
}
