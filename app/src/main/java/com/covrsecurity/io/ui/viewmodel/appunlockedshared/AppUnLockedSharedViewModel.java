package com.covrsecurity.io.ui.viewmodel.appunlockedshared;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.covrsecurity.io.domain.entity.LockTypeEntity;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.manager.AppUnLockedSharedStateHandler;
import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.BaseViewModel;
import com.covrsecurity.io.ui.viewmodel.base.Event;
import com.covrsecurity.io.ui.viewmodel.base.EventType;

public class AppUnLockedSharedViewModel extends BaseViewModel {

    private static final long NOT_LOCKED_TIME = 0L;
    private static final int MAX_ATTEMPTS_LEFT = 10;

    private final AppUnLockedSharedStateHandler stateHandle;
    private final MutableLiveData<BaseState<AppUnlockTimeEntity>> sharedLiveData = new MutableLiveData<>();

    public AppUnLockedSharedViewModel(AppUnLockedSharedStateHandler stateHandle) {
        this.stateHandle = stateHandle;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUnlockTimeEntity appUnLockedValue = stateHandle.getAppUnlockTimeEntity();
        if (appUnLockedValue != null) {
            sharedLiveData.setValue(BaseState.getSuccessInstance(appUnLockedValue));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUnlockTimeEntity appUnLockedValue = getAppUnlockTime();
        stateHandle.saveAppUnlockTimeEntity(appUnLockedValue);
    }

    @MainThread
    public void resetAppUnLockedValue() {
        setAppUnLockedValue(MAX_ATTEMPTS_LEFT);
    }

    @MainThread
    public void setAppUnLockedValue(int attemptsLeft) {
        AppUnlockTimeEntity appUnLockedValue = new AppUnlockTimeEntity(attemptsLeft, NOT_LOCKED_TIME, LockTypeEntity.NOT_LOCKED);
        setAppUnLockedValue(appUnLockedValue);
    }

    @MainThread
    public void setAppUnLockedValue(AppUnlockTimeEntity appUnLockedValue) {
        sharedLiveData.setValue(BaseState.getSuccessInstance(appUnLockedValue));
    }

    @MainThread
    @Nullable
    public AppUnlockTimeEntity getAppUnlockTime() {
        BaseState<AppUnlockTimeEntity> value = sharedLiveData.getValue();
        if (value != null && value.getEventType() == EventType.SUCCESS) {
            Event<Object> event = value.getEvent();
            return (AppUnlockTimeEntity) event.peekContent();
        }
        return null;
    }
}
