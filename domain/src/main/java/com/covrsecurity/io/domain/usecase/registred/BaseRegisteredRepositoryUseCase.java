package com.covrsecurity.io.domain.usecase.registred;

import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.BaseRepositoryUseCase;

public abstract class BaseRegisteredRepositoryUseCase extends BaseRepositoryUseCase {

    protected final RegisteredRepository repository;

    BaseRegisteredRepositoryUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }
}
