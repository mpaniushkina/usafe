package com.covrsecurity.io.ui.viewmodel.partnershipdetaills;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.MarkConnectionAsViewedRequestEntity;
import com.covrsecurity.io.domain.entity.response.MarkConnectionAsViewedResponseEntity;
import com.covrsecurity.io.domain.usecase.registred.MarkConnectionAsViewedUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.disposables.Disposable;

public class PartnershipDetailsViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<Boolean>> markConnectionAsViewedLiveData = new MutableLiveData<>();

    private final MarkConnectionAsViewedUseCase markConnectionAsViewedUseCase;

    @Nullable
    private Disposable disposable;

    public PartnershipDetailsViewModel(MarkConnectionAsViewedUseCase markConnectionAsViewedUseCase) {
        this.markConnectionAsViewedUseCase = markConnectionAsViewedUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void markConnectionAsViewed(String companyId) {
        disposable = markConnectionAsViewedUseCase.execute(new MarkConnectionAsViewedRequestEntity(companyId))
                .map(MarkConnectionAsViewedResponseEntity::isSucceeded)
                .doOnSubscribe(
                        disposable1 -> markConnectionAsViewedLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        response -> markConnectionAsViewedLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> markConnectionAsViewedLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }
}
