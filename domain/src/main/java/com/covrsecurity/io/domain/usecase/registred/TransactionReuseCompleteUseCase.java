package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.TransactionClaimRequestEntity;
import com.covrsecurity.io.domain.entity.response.TransactionClaimResponseEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class TransactionReuseCompleteUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<TransactionClaimResponseEntity, TransactionClaimRequestEntity> {

    @Inject
    public TransactionReuseCompleteUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<TransactionClaimResponseEntity> execute(@Nullable TransactionClaimRequestEntity request) {
        return repository.transactionReuseComplete(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
