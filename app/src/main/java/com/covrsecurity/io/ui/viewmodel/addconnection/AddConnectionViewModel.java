package com.covrsecurity.io.ui.viewmodel.addconnection;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.PostQrCodeRequestEntity;
import com.covrsecurity.io.domain.entity.request.QrCodeConnectionRequestEntity;
import com.covrsecurity.io.domain.entity.response.PostQrCodeResponseEntity;
import com.covrsecurity.io.domain.usecase.registred.AddConnectionUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.QrCodeConnectionUseCase;
import com.covrsecurity.io.sdk.response.QrCodeConnectionResponse;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class AddConnectionViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<PostQrCodeResponseEntity>> addConnectionLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<QrCodeConnectionResponse>> qrCodeConnectionLiveData = new MutableLiveData<>();

    private final AddConnectionUseCase addConnectionUseCase;
    private final QrCodeConnectionUseCase qrCodeConnectionUseCase;

    @Nullable
    private Disposable disposable;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public AddConnectionViewModel(AddConnectionUseCase addConnectionUseCase, QrCodeConnectionUseCase qrCodeConnectionUseCase) {
        this.addConnectionUseCase = addConnectionUseCase;
        this.qrCodeConnectionUseCase = qrCodeConnectionUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
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
