package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.MarkConnectionAsViewedRequestEntity;
import com.covrsecurity.io.domain.entity.response.MarkConnectionAsViewedResponseEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class MarkConnectionAsViewedUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<MarkConnectionAsViewedResponseEntity, MarkConnectionAsViewedRequestEntity> {

    @Inject
    MarkConnectionAsViewedUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<MarkConnectionAsViewedResponseEntity> execute(@Nullable MarkConnectionAsViewedRequestEntity request) {
        return repository.markConnectionAsViewed(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
