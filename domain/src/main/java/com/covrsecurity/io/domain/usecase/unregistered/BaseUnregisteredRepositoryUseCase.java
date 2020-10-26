package com.covrsecurity.io.domain.usecase.unregistered;

import com.covrsecurity.io.domain.usecase.base.BaseRepositoryUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;

public abstract class BaseUnregisteredRepositoryUseCase extends BaseRepositoryUseCase {

    protected final com.covrsecurity.io.domain.repository.UnregisteredRepository repository;

    BaseUnregisteredRepositoryUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }
}
