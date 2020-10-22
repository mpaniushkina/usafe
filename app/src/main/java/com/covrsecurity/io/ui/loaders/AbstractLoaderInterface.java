package com.covrsecurity.io.ui.loaders;

import java.io.IOException;

/**
 * Created by Lenovo on 16.05.2017.
 */

public interface AbstractLoaderInterface<T> {
    T action() throws IOException;
}
