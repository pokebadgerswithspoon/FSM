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
import fsm.FsmRuntime;
import fsm.Guard;
import fsm.State;

/**
 * A guard that will do the opposite thing as would wrapped guard do
 *
 * @author lauri
 */
class LogicalNotGuard implements Guard {

    private Guard guard;

    public LogicalNotGuard(Guard guard) {
        this.guard = guard;
    }

    @Override
    public boolean allow(Event event, State state, FsmRuntime runtime) {
        return !guard.allow(event, state, runtime);
    }
}
