/*
 * LogicalAndGuard.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import fsm.Event;
import fsm.Guard;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Guard that would allow an action if all inner guards allow that
 *
 * @author lauri
 */
class LogicalAndGuard<S,E,R> implements Guard<S,E,R> {

    private Collection<Guard<S,E,R>> guards = new HashSet();

    public LogicalAndGuard(Guard... guards) {
        this(Arrays.asList(guards));
    }

    public LogicalAndGuard(Collection<Guard<S,E,R>> guards) {
        this.guards = guards;
    }

    @Override
    public boolean allow(Event<E> event, S state, R runtime) {
        for (Guard guard : guards) {
            if (!guard.allow(event, state, runtime)) {
                return false;
            }
        }
        return true;
    }
}
