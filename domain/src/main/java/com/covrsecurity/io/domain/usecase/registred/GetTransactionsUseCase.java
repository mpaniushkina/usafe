package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.GetTransactionsRequestEntity;
import com.covrsecurity.io.domain.entity.response.TransactionsResponseEntity;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetTransactionsUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<TransactionsResponseEntity, GetTransactionsRequestEntity> {

    @Inject
    public GetTransactionsUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<TransactionsResponseEntity> execute(@Nullable GetTransactionsRequestEntity request) {
        return repository.getTransactions(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
