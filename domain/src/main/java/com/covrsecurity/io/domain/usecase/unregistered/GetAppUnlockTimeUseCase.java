package com.covrsecurity.io.domain.usecase.unregistered;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.LockTypeEntity;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.domain.usecase.base.SingleUseCase;
import com.covrsecurity.io.domain.executor.PostExecutionThread;
import com.covrsecurity.io.domain.executor.ThreadExecutor;
import com.covrsecurity.io.domain.repository.UnregisteredRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetAppUnlockTimeUseCase extends BaseUnregisteredRepositoryUseCase implements
        SingleUseCase<AppUnlockTimeEntity, Void> {

    public static final AppUnlockTimeEntity APP_UNLOCK_TIME_NO_VAULT_STORAGE = new AppUnlockTimeEntity(-1, -1, LockTypeEntity.NOT_LOCKED);

    @Inject
    public GetAppUnlockTimeUseCase(UnregisteredRepository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(repository, threadExecutor, postExecutionThread);
    }

    @Override
    public Single<AppUnlockTimeEntity> execute(@Nullable Void aVoid) {
        return repository.appUnlockTime()
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler);
    }
}
