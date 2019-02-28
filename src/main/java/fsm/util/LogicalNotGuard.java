/*
 * LogicalNotGuard.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import fsm.Event;
import fsm.Guard;

/**
 * A guard that will do the opposite thing as would wrapped guard do
 *
 * @author lauri
 */
class LogicalNotGuard<S,E,R> implements Guard<S,E,R> {

    private Guard guard;

    public LogicalNotGuard(Guard guard) {
        this.guard = guard;
    }

    @Override
    public boolean allow(Event<E> event, S state, R runtime) {
        return !guard.allow(event, state, runtime);
    }
}
