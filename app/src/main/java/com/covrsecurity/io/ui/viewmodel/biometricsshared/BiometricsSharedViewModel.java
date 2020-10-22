package com.covrsecurity.io.ui.viewmodel.biometricsshared;

import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.request.RegisterRecoveryRequestEntity;
import com.covrsecurity.io.model.BiometricsVerification;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;

public class BiometricsSharedViewModel extends BaseViewModel {

    public final MutableLiveData<BaseState<BiometricsVerification>> sharedLiveData = new MutableLiveData<>();
    public final MutableLiveData<BaseState<RegisterRecoveryRequestEntity>> registerRecoveryLiveData = new MutableLiveData<>();

}
