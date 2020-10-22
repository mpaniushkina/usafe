package com.covrsecurity.io.ui.viewmodel.base;

import androidx.annotation.IntDef;

import static com.covrsecurity.io.ui.viewmodel.base.EventType.ERROR;
import static com.covrsecurity.io.ui.viewmodel.base.EventType.PROCESSING;
import static com.covrsecurity.io.ui.viewmodel.base.EventType.SUCCESS;

@IntDef({PROCESSING, SUCCESS, ERROR})
public @interface EventType {
    int PROCESSING = 1;
    int SUCCESS = 2;
    int ERROR = 3;
}