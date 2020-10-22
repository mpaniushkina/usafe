package com.covrsecurity.io.ui.viewmodel.history;

import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.GetTransactionHistoryRequestEntity;
import com.covrsecurity.io.domain.entity.response.MarkHistoryAsViewedResponseEntity;
import com.covrsecurity.io.domain.entity.response.TransactionsResponseEntity;
import com.covrsecurity.io.domain.usecase.registred.GetTransactionHistoryUseCase;
import com.covrsecurity.io.domain.usecase.registred.MarkHistoryAsViewedUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.ui.viewmodel.base.Event;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.covrsecurity.io.utils.ConstantsUtils.DEFAULT_HISTORY_PAGE_SIZE;

public class HistoryViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<TransactionsResponseEntity>> historyLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<Boolean>> markHistoryAsViewedLiveData = new MutableLiveData<>();

    private final GetTransactionHistoryUseCase historyUseCase;
    private final MarkHistoryAsViewedUseCase markHistoryAsViewedUseCase;

    private CompositeDisposable disposables = new CompositeDisposable();

    public HistoryViewModel(GetTransactionHistoryUseCase historyUseCase, MarkHistoryAsViewedUseCase markHistoryAsViewedUseCase) {
        this.historyUseCase = historyUseCase;
        this.markHistoryAsViewedUseCase = markHistoryAsViewedUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }

    public void getHistory(int pageNumber) {
        Disposable disposable = Single.just(new GetTransactionHistoryRequestEntity(pageNumber, DEFAULT_HISTORY_PAGE_SIZE))
                .flatMap(historyUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> historyLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        response -> historyLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> historyLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }

    public void markHistoryAsViewed() {
        Disposable disposable = markHistoryAsViewedUseCase.execute(null)
                .map(MarkHistoryAsViewedResponseEntity::isSucceeded)
                .doOnSubscribe(
                        disposable1 -> markHistoryAsViewedLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        isSucceeded -> markHistoryAsViewedLiveData.setValue(BaseState.getSuccessInstance(isSucceeded)),
                        throwable -> markHistoryAsViewedLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }
}
