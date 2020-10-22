package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.response.GetPushNotificationsResponseEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.ObservableUseCase;

import javax.inject.Inject;

import io.reactivex.Observable;

public class GetPushNotificationsUseCase extends BaseRegisteredRepositoryUseCase implements
        ObservableUseCase<GetPushNotificationsResponseEntity, Void> {

    @Inject
    public GetPushNotificationsUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Observable<GetPushNotificationsResponseEntity> execute(@Nullable Void aVoid) {
        return repository.getPushNotifications()
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
