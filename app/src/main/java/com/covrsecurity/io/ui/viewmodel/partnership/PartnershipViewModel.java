package com.covrsecurity.io.ui.viewmodel.partnership;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.GetConnectionsRequestEntity;
import com.covrsecurity.io.domain.entity.request.PostQrCodeRequestEntity;
import com.covrsecurity.io.domain.entity.request.QrCodeClaimRequestEntity;
import com.covrsecurity.io.domain.entity.request.QrCodeConnectionRequestEntity;
import com.covrsecurity.io.domain.entity.request.TransactionClaimRequestEntity;
import com.covrsecurity.io.domain.entity.response.GetConnectionsResponseEntity;
import com.covrsecurity.io.domain.entity.response.PostQrCodeResponseEntity;
import com.covrsecurity.io.domain.entity.response.QrCodeClaimResponseEntity;
import com.covrsecurity.io.domain.entity.response.TransactionClaimResponseEntity;
import com.covrsecurity.io.domain.usecase.registred.AddConnectionUseCase;
import com.covrsecurity.io.domain.usecase.registred.GetConnectionsUseCase;
import com.covrsecurity.io.domain.usecase.registred.QrCodeClaimUseCase;
import com.covrsecurity.io.domain.usecase.registred.TransactionClaimCompleteUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.QrCodeConnectionUseCase;
import com.covrsecurity.io.sdk.response.QrCodeConnectionResponse;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.utils.ConstantsUtils;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class PartnershipViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<GetConnectionsResponseEntity>> connectionsLiveData = new MutableLiveData<>();

    public final MutableLiveData<BaseState<PostQrCodeResponseEntity>> addConnectionLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<QrCodeConnectionResponse>> qrCodeConnectionLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<QrCodeClaimResponseEntity>> qrCodeClaimLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<TransactionClaimResponseEntity>> transactionClaimLiveData = new MutableLiveData<>();

    private final GetConnectionsUseCase getConnectionsUseCase;
    private final AddConnectionUseCase addConnectionUseCase;
    private final QrCodeConnectionUseCase qrCodeConnectionUseCase;
    private final QrCodeClaimUseCase qrCodeClaimUseCase;
    private final TransactionClaimCompleteUseCase transactionClaimCompleteUseCase;

    @Nullable
    private Disposable disposable;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public PartnershipViewModel(GetConnectionsUseCase getConnectionsUseCase, AddConnectionUseCase addConnectionUseCase,
                                QrCodeConnectionUseCase qrCodeConnectionUseCase, QrCodeClaimUseCase qrCodeClaimUseCase, TransactionClaimCompleteUseCase transactionClaimCompleteUseCase) {
        this.getConnectionsUseCase = getConnectionsUseCase;
        this.addConnectionUseCase = addConnectionUseCase;
        this.qrCodeConnectionUseCase = qrCodeConnectionUseCase;
        this.qrCodeClaimUseCase = qrCodeClaimUseCase;
        this.transactionClaimCompleteUseCase = transactionClaimCompleteUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void getConnections(int pageNumber) {
        disposable = Single.just(new GetConnectionsRequestEntity(pageNumber, ConstantsUtils.DEFAULT_MERCHANT_PAGE_SIZE))
                .flatMap(getConnectionsUseCase::execute)
                .doOnSubscribe(
                        disposable1 -> connectionsLiveData.setValue(BaseState.getProcessingInstance())
                )
                .subscribe(
                        response -> connectionsLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> connectionsLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }

    public void addConnection(String qrCodeString) {
        disposable = Single.just(qrCodeString)
                .map(PostQrCodeRequestEntity::new)
                .flatMap(addConnectionUseCase::execute)
                .subscribe(
                        response -> addConnectionLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> addConnectionLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }

    public void verifyQrCodeClaim(String reference_id, int expires_at, String type, String status, String scopes) {
        disposable = Single.just(new QrCodeClaimRequestEntity(reference_id, expires_at, type, status, scopes))
                .flatMap(qrCodeClaimUseCase::execute)
                .subscribe(
                        response -> qrCodeClaimLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> qrCodeClaimLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }

    public void transactionClaimComplete(String referenceId, String companyRegPublicKey) {
        disposable = Single.just(new TransactionClaimRequestEntity(referenceId, companyRegPublicKey))
                .flatMap(transactionClaimCompleteUseCase::execute)
                .subscribe(
                        response -> transactionClaimLiveData.setValue(BaseState.getSuccessInstance(response)),
                        throwable -> transactionClaimLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }

    public void getQrCodeConnection(String companyUserId, String transactionId) {
        Disposable disposable = Single.just(new QrCodeConnectionRequestEntity(companyUserId, transactionId))
                .doOnSubscribe(disposable1 -> qrCodeConnectionLiveData.setValue(BaseState.getProcessingInstance()))
                .flatMapCompletable(qrCodeConnectionUseCase::execute)
                .subscribe(
                        () -> qrCodeConnectionLiveData.setValue(BaseState.getSuccessInstance(null)),
                        throwable -> qrCodeConnectionLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }
}
