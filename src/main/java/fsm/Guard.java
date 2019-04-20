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
public interface Guard<R> {

    public boolean allow(R runtime);

    static final Guard ALLOW = new Guard() {
        @Override
        public boolean allow(Object runtime) {
            return true;
        }
    };

    public static Guard not(Guard guard) {
        return new Guard() {
            @Override
            public boolean allow(Object runtime) {
                return !guard.allow(runtime);
            }
        };
    }

    public static Guard and(Collection<Guard> guards) {
        if(guards.size() == 1) {
            return guards.iterator().next();
        }
        return new Guard() {
            @Override
            public boolean allow(Object runtime) {
                for (Guard guard : guards) {
                    if (!guard.allow(runtime)) {
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
