/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
