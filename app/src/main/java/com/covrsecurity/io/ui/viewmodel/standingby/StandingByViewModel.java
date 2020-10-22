package com.covrsecurity.io.ui.viewmodel.standingby;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.domain.entity.request.GetConnectionsRequestEntity;
import com.covrsecurity.io.domain.entity.request.GetTransactionsRequestEntity;
import com.covrsecurity.io.domain.entity.request.TransactionConfirmationRequestEntity;
import com.covrsecurity.io.domain.entity.response.GetConnectionsResponseEntity;
import com.covrsecurity.io.domain.entity.response.GetUnreadHistoryCountResponseEntity;
import com.covrsecurity.io.domain.entity.response.TransactionsResponseEntity;
import com.covrsecurity.io.domain.usecase.registred.GetConnectionsUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetTransactionsUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetUnreadHistoryCountUseCase;
import com.covrsecurity.io.domain.usecase.registred.PostTransactionUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.CheckLockedUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.ui.viewmodel.base.Event;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static com.covrsecurity.io.utils.ConstantsUtils.DEFAULT_MERCHANT_PAGE_SIZE;
import static com.covrsecurity.io.utils.ConstantsUtils.DEFAULT_PAGE_NUMBER;
import static com.covrsecurity.io.utils.ConstantsUtils.DEFAULT_TRANSACTIONS_PAGE_SIZE;

public class StandingByViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<GetConnectionsResponseEntity>> connectionsLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<TransactionsResponseEntity>> transactionsLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<GetUnreadHistoryCountResponseEntity>> unreadHistoryCountLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<Void>> requestFinishedLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<TransactionEntity>> postTransactionLiveData = new MutableLiveData<>();

    public final MutableLiveData<BaseState<PostTransactionPending>> postedTransaction = new MutableLiveData<>();

    private static final int REQUESTS_COUNT = 3;

    private final CheckLockedUseCase checkLockedUseCase;
    private final GetConnectionsUseCase connectionsUseCase;
    private final GetTransactionsUseCase transactionsUseCase;
    private final GetUnreadHistoryCountUseCase unreadHistoryCountUseCase;
    private final PostTransactionUseCase postTransactionUseCase;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final AtomicInteger requestCounter = new AtomicInteger(0);

    public StandingByViewModel(CheckLockedUseCase checkLockedUseCase, GetConnectionsUseCase connectionsUseCase, GetTransactionsUseCase transactionsUseCase, GetUnreadHistoryCountUseCase unreadHistoryCountUseCase, PostTransactionUseCase postTransactionUseCase) {
        this.checkLockedUseCase = checkLockedUseCase;
        this.connectionsUseCase = connectionsUseCase;
        this.transactionsUseCase = transactionsUseCase;
        this.unreadHistoryCountUseCase = unreadHistoryCountUseCase;
        this.postTransactionUseCase = postTransactionUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }

    public void checkLocked() {
        Disposable disposable = checkLockedUseCase.execute(null)
                .subscribe(unlocked -> {
                    if (unlocked) {
                        loadAllData();
                    }
                }, Timber::e);
        disposables.add(disposable);
    }

    public void loadAllData() {
        requestCounter.set(0);
        getConnections(DEFAULT_PAGE_NUMBER);
        getTransactions(DEFAULT_PAGE_NUMBER);
        getGetUnreadHistoryCount();
    }

    public void getConnections(int pageNumber) {
        Disposable disposable = Single.just(new GetConnectionsRequestEntity(pageNumber, DEFAULT_MERCHANT_PAGE_SIZE))
                .flatMap(connectionsUseCase::execute)
                .subscribe(
                        response -> {
                            connectionsLiveData.setValue(BaseState.getSuccessInstance(response));
                            checkRequestsFinished();
                        },
                        throwable -> {
                            connectionsLiveData.setValue(BaseState.getErrorInstance(throwable));
                            checkRequestsFinished();
                        }
                );
        disposables.add(disposable);
    }

    public void getTransactions(int pageNumber) {
        Disposable disposable = Single.just(new GetTransactionsRequestEntity(pageNumber, DEFAULT_TRANSACTIONS_PAGE_SIZE))
                .flatMap(transactionsUseCase::execute)
                .subscribe(
                        response -> {
                            transactionsLiveData.setValue(BaseState.getSuccessInstance(response));
                            checkRequestsFinished();
                        },
                        throwable -> {
                            transactionsLiveData.setValue(BaseState.getErrorInstance(throwable));
                            checkRequestsFinished();
                        }
                );
        disposables.add(disposable);
    }

    public void getGetUnreadHistoryCount() {
        Disposable disposable = unreadHistoryCountUseCase.execute(null)
                .subscribe(
                        response -> {
                            unreadHistoryCountLiveData.setValue(BaseState.getSuccessInstance(response));
                            checkRequestsFinished();
                        },
                        throwable -> {
                            unreadHistoryCountLiveData.setValue(BaseState.getErrorInstance(throwable));
                            checkRequestsFinished();
                        }
                );
        disposables.add(disposable);
    }

    public void postTransaction(TransactionEntity transaction, boolean accept, byte[] bioMetricData) {
        Disposable disposable = Single.just(new TransactionConfirmationRequestEntity(transaction, accept, bioMetricData))
                .doOnSubscribe(disposable1 -> postTransactionLiveData.setValue(BaseState.getProcessingInstance()))
                .flatMap(postTransactionUseCase::execute)
                .subscribe(
                        response -> postTransactionLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> postTransactionLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }

    private void checkRequestsFinished() {
        int requestsFinished = requestCounter.incrementAndGet();
        Timber.w("checkRequestsFinished " + requestsFinished);
        if (REQUESTS_COUNT == requestsFinished) {
            requestFinishedLiveData.setValue(BaseState.getSuccessInstance(null));
        }
    }

    public void setPostedTransaction(TransactionEntity pendingTransaction, boolean accept) {
        final PostTransactionPending transactionPending = new PostTransactionPending(pendingTransaction, accept);
        this.postedTransaction.setValue(BaseState.getSuccessInstance(transactionPending));
    }

    @Nullable
    public PostTransactionPending getPostedTransaction() {
        final BaseState<PostTransactionPending> value = postedTransaction.getValue();
        if (value != null) {
            final Event<PostTransactionPending> event = value.getEvent();
            if (!event.isHasBeenHandled()) {
                return event.peekContent();
            }
        }
        return null;
    }
}
