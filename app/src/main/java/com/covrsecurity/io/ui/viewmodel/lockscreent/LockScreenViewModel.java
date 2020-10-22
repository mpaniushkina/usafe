package com.covrsecurity.io.ui.viewmodel.lockscreent;

import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.UnLockRequestEntity;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.domain.usecase.unregistered.GetAppUnlockTimeUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.UnLockUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class LockScreenViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<AppUnlockTimeEntity>> getAppUnlockTimeLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<Boolean>> unLockLiveData = new MutableLiveData<>();

    private final GetAppUnlockTimeUseCase getAppUnlockTimeUseCase;
    private final UnLockUseCase unLockUseCase;

    private CompositeDisposable disposables = new CompositeDisposable();

    public LockScreenViewModel(GetAppUnlockTimeUseCase getAppUnlockTimeUseCase, UnLockUseCase unLockUseCase) {
        this.getAppUnlockTimeUseCase = getAppUnlockTimeUseCase;
        this.unLockUseCase = unLockUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.dispose();
    }

    public void getAppUnlockTime() {

        Disposable disposable = getAppUnlockTimeUseCase.execute(null)
                .doOnSubscribe(disposable1 -> getAppUnlockTimeLiveData.setValue(BaseState.getProcessingInstance()))
                .subscribe(
                        registered -> getAppUnlockTimeLiveData.setValue(BaseState.getSuccessInstance(registered)),
                        throwable -> getAppUnlockTimeLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }

    public void unLock(char[] covrCode) {
        Disposable disposable = Single.just(new UnLockRequestEntity(covrCode))
                .doOnSubscribe(disposable1 -> unLockLiveData.setValue(BaseState.getProcessingInstance()))
                .flatMap(unLockUseCase::execute)
                .subscribe(
                        registered -> unLockLiveData.setValue(BaseState.getSuccessInstance(registered)),
                        throwable -> unLockLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
        disposables.add(disposable);
    }
}
