package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.GetTransactionHistoryRequestEntity;
import com.covrsecurity.io.domain.entity.response.TransactionsResponseEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetTransactionHistoryUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<TransactionsResponseEntity, GetTransactionHistoryRequestEntity> {

    @Inject
    public GetTransactionHistoryUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<TransactionsResponseEntity> execute(@Nullable GetTransactionHistoryRequestEntity request) {
        return repository.getTransactionHistory(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
