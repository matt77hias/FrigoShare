package com.frigoshare.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FilterUtils {

    public static <T> List<T> filter(Iterable<T> toFilter, Filter<T> filter) {
        return filter(toFilter.iterator(), filter);
    }

    public static <T> List<T> filter(Iterator<T> toFilter, Filter<T> filter) {
        List<T> filtered = new ArrayList<T>();
        while (toFilter.hasNext()) {
            T t = toFilter.next();
            if (filter.accept(t)) {
                filtered.add(t);
            }
        }
        return filtered;
    }
}
