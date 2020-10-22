package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.RegisterRecoveryRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;

import javax.inject.Inject;

import io.reactivex.Completable;

public class RegisterBiometricRecoveryUseCase extends BaseRegisteredRepositoryUseCase implements
        CompletableUseCase<RegisterRecoveryRequestEntity> {

    @Inject
    public RegisterBiometricRecoveryUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable RegisterRecoveryRequestEntity requestEntity) {
        return repository.registerBiometricRecovery(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
