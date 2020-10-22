package com.covrsecurity.io.manager;

import androidx.lifecycle.SavedStateHandle;

import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;

public class AppUnLockedSharedStateHandler {

    private static final String APP_UNLOCK_TIME_ENTITY_KEY = "APP_UNLOCK_TIME_ENTITY_KEY";

    private final SavedStateHandle savedStateHandle = new SavedStateHandle();

    public void saveAppUnlockTimeEntity(AppUnlockTimeEntity unlockTime) {
        savedStateHandle.set(APP_UNLOCK_TIME_ENTITY_KEY, unlockTime);
    }

    public AppUnlockTimeEntity getAppUnlockTimeEntity() {
        return savedStateHandle.get(APP_UNLOCK_TIME_ENTITY_KEY);
    }
}
