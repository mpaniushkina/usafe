package com.covrsecurity.io.ui.viewmodel.base;

public abstract class BaseState<T> {

    public static <T1> BaseState<T1> getProcessingInstance() {
        return (BaseState<T1>) new StateProcessing();
    }

    public static <T1> BaseState<T1> getSuccessInstance(T1 result) {
        return new StateSuccess<>(result);
    }

    public static <T1> BaseState<T1> getErrorInstance(Throwable throwable) {
        return (BaseState<T1>) new StateError(throwable);
    }

    private final Event<T> data;
    @EventType
    private final int eventType;

    BaseState(T dataContent, int eventType) {
        this.data = new Event<>(dataContent);
        this.eventType = eventType;
    }

    Event<T> getData() {
        return data;
    }

    @EventType
    public int getEventType() {
        return eventType;
    }

    public abstract <T2> Event<T2> getEvent();

    public final static class StateProcessing extends BaseState<Void> {

        private StateProcessing() {
            super(null, EventType.PROCESSING);
        }

        @Override
        public Event<Void> getEvent() {
            return getData();
        }
    }

    public final static class StateSuccess<Result> extends BaseState<Result> {

        private StateSuccess(Result result) {
            super(result, EventType.SUCCESS);
        }

        @Override
        public Event<Result> getEvent() {
            return getData();
        }
    }

    public final static class StateError extends BaseState<Throwable> {

        private StateError(Throwable data) {
            super(data, EventType.ERROR);
        }

        @Override
        public Event<Throwable> getEvent() {
            return getData();
        }
    }
}