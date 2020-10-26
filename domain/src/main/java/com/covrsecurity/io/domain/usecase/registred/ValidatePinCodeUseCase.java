package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.ValidatePinCodeRequestEntity;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;

import javax.inject.Inject;

import io.reactivex.Completable;

public class ValidatePinCodeUseCase extends BaseRegisteredRepositoryUseCase implements
        CompletableUseCase<ValidatePinCodeRequestEntity> {

    @Inject
    public ValidatePinCodeUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable ValidatePinCodeRequestEntity request) {
        return repository.validatePinCode(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
