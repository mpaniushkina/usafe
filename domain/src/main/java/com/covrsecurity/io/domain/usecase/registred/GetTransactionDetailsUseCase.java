package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.domain.entity.request.GetTransactionDetailsRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetTransactionDetailsUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<TransactionEntity, GetTransactionDetailsRequestEntity> {

    @Inject
    public GetTransactionDetailsUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<TransactionEntity> execute(@Nullable GetTransactionDetailsRequestEntity request) {
        return repository.getTransactionDetails(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
