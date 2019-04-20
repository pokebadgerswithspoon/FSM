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

    public boolean allow(R runtime, P payload);

    static final Guard ALLOW = (Guard) (Object runtime, Object payload) -> true;

    public static Guard not(Guard guard) {
        return (Guard) (Object runtime, Object payload) -> !guard.allow(runtime, payload);
    }

    public static Guard and(Collection<Guard> guards) {
        if(guards.size() == 1) {
            return guards.iterator().next();
        }
        return new Guard() {
            @Override
            public boolean allow(Object runtime, Object payload) {
                for (Guard guard : guards) {
                    if (!guard.allow(runtime, payload)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
    public static Guard and(Guard... guards) {
        return and(Arrays.asList(guards));
    }
}
