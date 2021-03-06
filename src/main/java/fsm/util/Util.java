/*
 * Util.java
 * Created on 20 Apr 2019 12:39:09
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author lauri
 */
public class Util {

    public static <T> Iterable<T> iterableNonNulls(T... items) {
        return Stream.of(items)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
