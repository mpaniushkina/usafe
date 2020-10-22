package com.covrsecurity.io.domain.usecase.base;

import androidx.annotation.Nullable;

import io.reactivex.Observable;

public interface ObservableUseCase<Results, Params> extends BaseUseCase {

    Observable<Results> execute(@Nullable Params params);
}
