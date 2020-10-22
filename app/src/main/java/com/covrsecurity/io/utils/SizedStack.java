package com.covrsecurity.io.utils;

import java.util.Stack;

/**
 * Created by Egor on 31.08.2016.
 */
public class SizedStack<T> extends Stack<T> {

    private final int MAX_SIZE;

    public SizedStack(int size) {
        super();
        MAX_SIZE = size;
    }

    @Override
    public T push(T object) {
        if (this.size() >= MAX_SIZE) {
            return object;
        } else {
            return super.push(object);
        }
    }

    @Override
    public synchronized T pop() {
        if (size() != 0) {
            return super.pop();
        } else {
            return null;
        }
    }

    public synchronized T getSafe(int index) {
        if (index >= size()) {
            return null;
        } else {
            return get(index);
        }
    }

}