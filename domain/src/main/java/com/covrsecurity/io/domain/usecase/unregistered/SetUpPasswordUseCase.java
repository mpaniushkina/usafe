package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.SetUpPasswordRequestEntity;
import com.covrsecurity.io.domain.usecase.base.CompletableUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;

import javax.inject.Inject;

import io.reactivex.Completable;

public class SetUpPasswordUseCase extends BaseUnregisteredRepositoryUseCase implements
        CompletableUseCase<SetUpPasswordRequestEntity> {

    @Inject
    public SetUpPasswordUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Completable execute(@Nullable SetUpPasswordRequestEntity requestEntity) {
        return repository.setUpPassword(requestEntity)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
