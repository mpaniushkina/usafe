package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.NotificationHubRegistrationRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;

import javax.inject.Inject;

import io.reactivex.Completable;

public class NotificationHubRegistrationUseCase extends BaseRegisteredRepositoryUseCase implements CompletableUseCase<NotificationHubRegistrationRequestEntity> {

    @Inject
    public NotificationHubRegistrationUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable NotificationHubRegistrationRequestEntity request) {
        return repository.notificationHubRegistration(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
