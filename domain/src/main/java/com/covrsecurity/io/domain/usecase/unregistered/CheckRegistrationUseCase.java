package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class CheckRegistrationUseCase extends BaseUnregisteredRepositoryUseCase implements
        SingleUseCase<Boolean, Void> {

    @Inject
    public CheckRegistrationUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<Boolean> execute(@Nullable Void aVoid) {
        return repository.isRegistered()
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
