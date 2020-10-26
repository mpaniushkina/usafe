package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.response.MarkHistoryAsViewedResponseEntity;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class MarkHistoryAsViewedUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<MarkHistoryAsViewedResponseEntity, Void> {

    @Inject
    MarkHistoryAsViewedUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<MarkHistoryAsViewedResponseEntity> execute(@Nullable Void request) {
        return repository.markHistoryAsViewed()
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
