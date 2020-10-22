package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.ChangePinCodeRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;

import javax.inject.Inject;

import io.reactivex.Completable;

public class ChangePinCodeUseCase extends BaseRegisteredRepositoryUseCase implements
        CompletableUseCase<ChangePinCodeRequestEntity> {

    @Inject
    public ChangePinCodeUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable ChangePinCodeRequestEntity requestEntity) {
        return repository.changePinCode(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
