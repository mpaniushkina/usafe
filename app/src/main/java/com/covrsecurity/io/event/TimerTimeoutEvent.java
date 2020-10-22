package com.covrsecurity.io.event;

/**
 * Created by DellUser on 22.12.2016.
 */
public class TimerTimeoutEvent {

    private int count;

    public TimerTimeoutEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "TimerTimeoutEvent{" +
                "count=" + count +
                '}';
    }

}