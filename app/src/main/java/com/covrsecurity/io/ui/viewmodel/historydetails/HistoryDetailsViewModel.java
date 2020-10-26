package com.covrsecurity.io.ui.viewmodel.historydetails;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.domain.entity.request.GetTransactionDetailsRequestEntity;
import com.covrsecurity.io.domain.usecase.registred.GetTransactionDetailsUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class HistoryDetailsViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<TransactionEntity>> getTransactionDetailsLiveData = new MutableLiveData<>();

    private final GetTransactionDetailsUseCase getTransactionDetailsUseCase;

    @Nullable
    private Disposable disposable;

    public HistoryDetailsViewModel(GetTransactionDetailsUseCase getTransactionDetailsUseCase) {
        this.getTransactionDetailsUseCase = getTransactionDetailsUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void getTransactionDetails(String transactionId, String companyId) {
        disposable = Single.just(new GetTransactionDetailsRequestEntity(transactionId, companyId))
                .flatMap(getTransactionDetailsUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> getTransactionDetailsLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        response -> getTransactionDetailsLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> getTransactionDetailsLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }
}
