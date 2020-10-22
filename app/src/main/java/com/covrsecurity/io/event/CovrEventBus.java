package com.covrsecurity.io.event;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by alex on 6.5.16.
 */
public class CovrEventBus implements EventProvider {

    @Override
    public void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    @Override
    public void post(Object event) {
        EventBus.getDefault().post(event);
    }

    @Override
    public void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }

    @Override
    public void removeStickyEvent(Object event) {
        EventBus.getDefault().removeStickyEvent(event);
    }

}
