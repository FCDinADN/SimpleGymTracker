package com.runApp.utils;

/**
 * Created by vladtamas on 15/07/14.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Provides static methods for creating mutable {@code Maps} instances easily.
 */
public class Maps {
    /**
     * Creates a {@code HashMap} instance.
     *
     * @return a newly-created, initially-empty {@code HashMap}
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V> List<V> flatten(HashMap<K, V> map) {
        List<V> result = Lists.newArrayList();

        Iterator<K> it = map.keySet().iterator();
        while (it.hasNext()) {
            result.add(map.get(it.next()));
        }
        return result;
    }
}
