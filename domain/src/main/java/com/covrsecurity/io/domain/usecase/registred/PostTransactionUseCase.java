package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.domain.entity.request.TransactionConfirmationRequestEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class PostTransactionUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<TransactionEntity, TransactionConfirmationRequestEntity> {

    @Inject
    public PostTransactionUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<TransactionEntity> execute(@Nullable TransactionConfirmationRequestEntity request) {
        return repository.postTransaction(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
