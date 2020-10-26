package com.covrsecurity.io.domain.usecase.registred;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.request.PostQrCodeRequestEntity;
import com.covrsecurity.io.domain.entity.response.PostQrCodeResponseEntity;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.RegisteredRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class AddConnectionUseCase extends BaseRegisteredRepositoryUseCase implements
        SingleUseCase<PostQrCodeResponseEntity, PostQrCodeRequestEntity> {

    @Inject
    public AddConnectionUseCase(RegisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<PostQrCodeResponseEntity> execute(@Nullable PostQrCodeRequestEntity request) {
        return repository.addConnection(request)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
