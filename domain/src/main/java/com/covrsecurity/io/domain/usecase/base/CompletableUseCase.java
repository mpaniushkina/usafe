package com.covrsecurity.io.domain.usecase.base;

import androidx.annotation.Nullable;

import io.reactivex.Completable;

public interface CompletableUseCase<Params> extends BaseUseCase {

    Completable execute(@Nullable Params params);
}
