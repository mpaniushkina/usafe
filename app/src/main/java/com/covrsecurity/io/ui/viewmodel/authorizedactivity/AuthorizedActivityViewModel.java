package com.covrsecurity.io.ui.viewmodel.authorizedactivity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.response.GetPushNotificationsResponseEntity;
import com.covrsecurity.io.domain.usecase.identity.IsApprovedUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetPushNotificationsUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.Event;
import com.covrsecurity.io.ui.viewmodel.baseactivity.BaseActivityViewModel;

import io.reactivex.disposables.Disposable;

public class AuthorizedActivityViewModel extends BaseActivityViewModel {

    public final MutableLiveData<BaseState<GetPushNotificationsResponseEntity>> pushNotificationsLiveData = new MutableLiveData<>();

    private final GetPushNotificationsUseCase pushNotificationsUseCase;

    @Nullable
    private Disposable disposable;

    public AuthorizedActivityViewModel(IsApprovedUseCase isApprovedUseCase, GetPushNotificationsUseCase pushNotificationsUseCase) {
        super(isApprovedUseCase);
        this.pushNotificationsUseCase = pushNotificationsUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void subscribeToPushNotifications() {
        disposable = pushNotificationsUseCase.execute(null)
                .subscribe(
                        response -> pushNotificationsLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> pushNotificationsLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }
}
