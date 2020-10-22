package com.covrsecurity.io.domain.usecase.base;

import androidx.annotation.Nullable;

import io.reactivex.Single;

public interface SingleUseCase<Results, Params> extends BaseUseCase {

    Single<Results> execute(@Nullable Params params);
}
