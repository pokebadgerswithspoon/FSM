/*
 * Guard.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author lauri
 */
public interface Guard<R, P> {

    boolean allow(R runtime, P payload);

    Guard ALLOW = (runtime, payload) -> true;

    static Guard not(Guard guard) {
        return (runtime, payload) -> !guard.allow(runtime, payload);
    }

    static Guard and(Guard... guards) {
        return and(Arrays.asList(guards));
    }

    static Guard and(Collection<Guard> guards) {
        if(guards.size() == 1) {
            return guards.iterator().next();
        }
        return (runtime, payload) -> {
            for (Guard guard : guards) {
                if (!guard.allow(runtime, payload)) {
                    return false;
                }
            }
            return true;
        };
    }
}
