package com.covrsecurity.io.ui.viewmodel.baseactivity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.response.IsApprovedResponseEntity;
import com.covrsecurity.io.domain.usecase.identity.IsApprovedUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.disposables.Disposable;

public class BaseActivityViewModel extends BaseViewModel {

    @Nullable
    private Disposable disposable;

    public final MutableLiveData<BaseState<IsApprovedResponseEntity>> pushNotificationsLiveData = new MutableLiveData<>();

    private final IsApprovedUseCase isApprovedUseCase;

    public BaseActivityViewModel(IsApprovedUseCase isApprovedUseCase) {
        this.isApprovedUseCase = isApprovedUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void isApproved() {
        disposable = isApprovedUseCase.execute(null)
                .subscribe(
                        response -> pushNotificationsLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> pushNotificationsLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }
}
