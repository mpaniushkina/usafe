package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.GetConnectionsRequestEntity;
import com.covrsecurity.io.domain.entity.response.GetConnectionsResponseEntity;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetConnectionsUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<GetConnectionsResponseEntity, GetConnectionsRequestEntity> {

    @Inject
    public GetConnectionsUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<GetConnectionsResponseEntity> execute(@Nullable GetConnectionsRequestEntity request) {
        return repository.getConnections(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
