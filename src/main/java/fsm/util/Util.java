/*
 * Util.java
 * Created on 20 Apr 2019 12:39:09
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author lauri
 */
public class Util {

    public static <T> Iterable<T> iterableNonNulls(T... items) {
        Deque<T> result = new LinkedList<>();
        for (T item : items) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }
}
