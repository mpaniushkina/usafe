package com.covrsecurity.io.ui.viewmodel.base.observer;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.covrsecurity.io.ui.viewmodel.base.BaseState;
import com.covrsecurity.io.ui.viewmodel.base.Event;
import com.covrsecurity.io.ui.viewmodel.base.EventType;

public class BaseObserver<T> implements Observer<BaseState<T>> {

    @Nullable
    private final BaseObserverOnProcessing onProcessing;
    @Nullable
    private final BaseObserverOnSuccess<T> onSuccess;
    @Nullable
    private final BaseObserverOnError onError;

    public BaseObserver(@Nullable BaseObserverOnProcessing onProcessing, @Nullable BaseObserverOnSuccess<T> onSuccess, @Nullable BaseObserverOnError onError) {
        this.onProcessing = onProcessing;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    public void onChanged(BaseState<T> baseState) {
        final Event<?> stateEvent = baseState.getEvent();
        if (!stateEvent.isHasBeenHandled()) {
            final int eventType = baseState.getEventType();
            final Object content = stateEvent.peekContent();
            switch (eventType) {
                case EventType.PROCESSING:
                    if (onProcessing != null) {
                        onProcessing.onProcessing();
                    }
                    break;
                case EventType.SUCCESS:
                    final T result = (T) content;
                    if (onSuccess != null) {
                        onSuccess.onSuccess(result);
                    }
                    break;
                case EventType.ERROR:
                    final Throwable error = (Throwable) content;
                    if (onError != null) {
                        onError.onError(error);
                    }
                    break;
            }
        }
    }
}