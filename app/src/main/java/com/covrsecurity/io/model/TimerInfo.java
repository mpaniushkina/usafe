package com.covrsecurity.io.model;

/**
 * Created by abaranov on 1.5.16.
 */
public class TimerInfo {
    private final long startTime;
    private final long dueTime;

    public TimerInfo(long startTime, long dueTime) {
        this.startTime = startTime;
        this.dueTime = dueTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDueTime() {
        return dueTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimerInfo)) return false;

        TimerInfo timerInfo = (TimerInfo) o;

        if (startTime != timerInfo.startTime) return false;
        return dueTime == timerInfo.dueTime;
    }

    @Override
    public int hashCode() {
        int result = (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (dueTime ^ (dueTime >>> 32));
        return result;
    }
}
