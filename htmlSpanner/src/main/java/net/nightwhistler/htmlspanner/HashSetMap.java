package net.nightwhistler.htmlspanner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yegor on 17.11.2016.
 */

public class HashSetMap<K, V> extends HashMap<K, V> {

    public HashSetMap() {
    }

    public HashSetMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public V putIfAbsent(K key, V value) {
        V v = get(key);
        if (v == null) {
            v = put(key, value);
        }

        return v;
    }

}