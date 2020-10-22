package com.covrsecurity.io.event;

import org.greenrobot.greendao.async.AsyncOperation;

/**
 * Created by Lenovo on 01.06.2017.
 */
@Deprecated
public class DatabaseOperationCompletedEvent {

    private AsyncOperation.OperationType mOperationType;
    private long mRecordId;

    public DatabaseOperationCompletedEvent(AsyncOperation.OperationType operationType, long recordId) {
        mOperationType = operationType;
        mRecordId = recordId;
    }

    public AsyncOperation.OperationType getOperationType() {
        return mOperationType;
    }

    public void setOperationType(AsyncOperation.OperationType operationType) {
        mOperationType = operationType;
    }

    public long getRecordId() {
        return mRecordId;
    }

    public void setRecordId(long recordId) {
        mRecordId = recordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseOperationCompletedEvent that = (DatabaseOperationCompletedEvent) o;

        if (mRecordId != that.mRecordId) return false;
        return mOperationType == that.mOperationType;

    }

    @Override
    public int hashCode() {
        int result = mOperationType != null ? mOperationType.hashCode() : 0;
        result = 31 * result + (int) (mRecordId ^ (mRecordId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DatabaseOperationCompletedEvent{" +
                "mOperationType=" + mOperationType +
                ", mRecordId=" + mRecordId +
                '}';
    }
}
