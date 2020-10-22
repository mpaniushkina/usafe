package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.RegisterRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;

import javax.inject.Inject;

import io.reactivex.Completable;

public class RegisterUseCase extends BaseUnregisteredRepositoryUseCase implements
        CompletableUseCase<RegisterRequestEntity> {

    @Inject
    public RegisterUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable RegisterRequestEntity requestEntity) {
        return repository.register(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
