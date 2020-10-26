package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.AssessPinCodeStrengthRequestEntity;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;

import javax.inject.Inject;

import io.reactivex.Completable;

public class AssessPinCodeStrengthUseCase extends BaseUnregisteredRepositoryUseCase implements
        CompletableUseCase<AssessPinCodeStrengthRequestEntity> {

    @Inject
    public AssessPinCodeStrengthUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable AssessPinCodeStrengthRequestEntity requestEntity) {
        return repository.assessPinCodeStrength(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
