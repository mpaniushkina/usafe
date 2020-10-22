package com.covrsecurity.io.ui.viewmodel.splash;

import android.app.Activity;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.common.ZipUtils;
import com.covrsecurity.io.domain.entity.request.AttestationRequestEntity;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.domain.usecase.DelayUseCase;
import com.covrsecurity.io.domain.usecase.InitializeTrueTimeUseCase;
import com.covrsecurity.io.domain.usecase.identity.GetNonceUseCase;
import com.covrsecurity.io.domain.usecase.identity.VerifyUseCase;
import com.covrsecurity.io.domain.usecase.playservices.AttestSafetyNetUseCase;
import com.covrsecurity.io.domain.usecase.playservices.CheckPlayServicesUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.CheckRegistrationUseCase;
import com.covrsecurity.io.domain.usecase.unregistered.GetAppUnlockTimeUseCase;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.utils.DeviceIDUtils;

import java.util.Date;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

import static com.covrsecurity.io.common.ConstantsUtils.CUSTOM_USER_AGENT;
import static com.covrsecurity.io.domain.usecase.unregistered.GetAppUnlockTimeUseCase.APP_UNLOCK_TIME_NO_VAULT_STORAGE;

public class SplashViewModel extends BaseViewModel {

    private static final int RETRY_COUNT = 5;
    private static final long MIN_DELAY = 2000L;

    public final MutableLiveData<BaseState<Boolean>> authLiveData = new MutableLiveData<>();

    private final CheckPlayServicesUseCase checkPlayServicesUseCase;
    private final GetNonceUseCase getNonceUseCase;
    private final AttestSafetyNetUseCase attestSafetyNetUseCase;
    private final VerifyUseCase verifyUseCase;
    private final InitializeTrueTimeUseCase initializeTrueTimeUseCase;
    private final CheckRegistrationUseCase checkRegistrationUseCase;
    private final GetAppUnlockTimeUseCase getAppUnlockTimeUseCase;
    private final DelayUseCase delayUseCase;

    @Nullable
    private Disposable disposable;

    public SplashViewModel(CheckPlayServicesUseCase checkPlayServicesUseCase, GetNonceUseCase getNonceUseCase, AttestSafetyNetUseCase attestSafetyNetUseCase, VerifyUseCase verifyUseCase, InitializeTrueTimeUseCase initializeTrueTimeUseCase, CheckRegistrationUseCase checkRegistrationUseCase, GetAppUnlockTimeUseCase getAppUnlockTimeUseCase, DelayUseCase delayUseCase) {
        this.checkPlayServicesUseCase = checkPlayServicesUseCase;
        this.getNonceUseCase = getNonceUseCase;
        this.attestSafetyNetUseCase = attestSafetyNetUseCase;
        this.verifyUseCase = verifyUseCase;
        this.initializeTrueTimeUseCase = initializeTrueTimeUseCase;
        this.checkRegistrationUseCase = checkRegistrationUseCase;
        this.getAppUnlockTimeUseCase = getAppUnlockTimeUseCase;
        this.delayUseCase = delayUseCase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void doAuth(Activity activity) {

        final Single<Boolean> verificationSingle = checkPlayServicesUseCase.execute(activity)
                .flatMap(playServicesAvailable -> getNonceUseCase.execute(CUSTOM_USER_AGENT))
                .flatMap(attestSafetyNetUseCase::execute)
                .map(statement -> new AttestationRequestEntity(statement, DeviceIDUtils.getUniquePsuedoID()))
                .flatMap(verifyUseCase::execute);

        final Single<Date> trueTimeSingle = initializeTrueTimeUseCase.execute(RETRY_COUNT);

        final Single<Pair<Boolean, Throwable>> doAuthSingle = ZipUtils.zip(
                verificationSingle,
                trueTimeSingle,
                (verified, date) -> verified
        )
                .flatMap(verified -> checkRegistrationUseCase.execute(null))
                .map(registered -> new Pair<Boolean, Throwable>(registered, null))
                .onErrorReturn(throwable -> new Pair<>(null, throwable));

        final Single<Boolean> delaySingle = delayUseCase.execute(MIN_DELAY).toSingleDefault(true);

        final Single<AppUnlockTimeEntity> appUnlockTimeSingle = getAppUnlockTimeUseCase.execute(null)
                .onErrorReturnItem(APP_UNLOCK_TIME_NO_VAULT_STORAGE);

        disposable = Single.zip(
                doAuthSingle,
                delaySingle,
                appUnlockTimeSingle,
                (registeredThrowablePair, alwaysTrue, appUnlockTimeEntity) -> registeredThrowablePair
        )
                .flatMap(registeredThrowablePair -> {
                    if (registeredThrowablePair.first != null) {
                        return Single.just(registeredThrowablePair.first);
                    } else {
                        return Single.error(registeredThrowablePair.second);
                    }
                })
                .subscribe(
                        registered -> authLiveData.setValue(BaseState.getSuccessInstance(registered)),
                        throwable -> authLiveData.setValue(BaseState.getErrorInstance(throwable))
                );
    }
}
