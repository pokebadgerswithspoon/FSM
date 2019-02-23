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
import fsm.FsmRuntime;
import fsm.Guard;
import fsm.State;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Guard that would allow an action if all inner guards allow that
 *
 * @author lauri
 */
class LogicalAndGuard implements Guard {

    private Collection<Guard> guards = new HashSet();

    public LogicalAndGuard(Guard... guards) {
        this(Arrays.asList(guards));
    }

    public LogicalAndGuard(Collection<Guard> guards) {
        this.guards = guards;
    }

    @Override
    public boolean allow(Event event, State state, FsmRuntime runtime) {
        for (Guard guard : guards) {
            if (!guard.allow(event, state, runtime)) {
                return false;
            }
        }
        return true;
    }
}
