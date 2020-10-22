package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.response.GetUnreadHistoryCountResponseEntity;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetUnreadHistoryCountUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<GetUnreadHistoryCountResponseEntity, Void> {

    @Inject
    public GetUnreadHistoryCountUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<GetUnreadHistoryCountResponseEntity> execute(@Nullable Void request) {
        return repository.getUnreadHistoryCount()
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
