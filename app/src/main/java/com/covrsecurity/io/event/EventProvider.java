package com.covrsecurity.io.event;

/**
 * Created by alex on 6.5.16.
 */
public interface EventProvider {

    void register(Object subscriber);
    void unregister(Object subscriber);
    void post(Object event);
    void postSticky(Object event);
    void removeStickyEvent(Object event);

}
