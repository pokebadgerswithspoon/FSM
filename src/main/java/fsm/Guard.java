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
import java.util.List;

/**
 *
 * @author lauri
 */
public interface Guard<R, P> {

    boolean allow(R runtime, P payload);

    Guard ALLOW = (runtime, payload) -> true;

    static <R,P> Guard<R,P> allow() {
        return (r, p) -> true;
    }

    static <R,P> Guard<R, P> not(Guard<R,P> guard) {
        return (R runtime, P payload) -> !guard.allow(runtime, payload);
    }

    static <R, P> Guard<R, P> and(Guard<R, P>... guards) {
        return and(Arrays.asList(guards));
    }

    static <R, P> Guard<R, P> and(Collection<Guard<R,P>> guards) {
        if(guards.size() == 1) {
            return guards.iterator().next();
        }
        return (runtime, payload) -> {
            for (Guard<R, P> guard : guards) {
                if (!guard.allow(runtime, payload)) {
                    return false;
                }
            }
            return true;
        };
    }

    static <R, P> Guard<R, P> or(Guard<R, P>... guards) {
        return or(Arrays.asList(guards));
    }

    static <R, P> Guard<R,P> or(List<Guard<R,P>> guards) {
        return (r, p) -> guards.stream()
                    .anyMatch(g -> g.allow(r, p));
    }

    static <R,P> Guard<R,P> unless(Guard<R,P>... guards) {
        return not(or(guards));
    }
}
