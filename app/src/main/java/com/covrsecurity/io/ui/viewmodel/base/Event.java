package com.covrsecurity.io.ui.viewmodel.base;

public class Event<T> {

    private final T content;

    private volatile boolean hasBeenHandled;

    public Event(T content) {
        this.content = content;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    public T peekContent() {
        hasBeenHandled = true;
        return content;
    }

    @Override
    public String toString() {
        return "Event{" +
                "content=" + content +
                ", hasBeenHandled=" + hasBeenHandled +
                '}';
    }
}
