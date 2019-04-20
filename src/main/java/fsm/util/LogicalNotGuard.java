/*
 * LogicalNotGuard.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import fsm.Guard;

/**
 * A guard that will do the opposite thing as would wrapped guard do
 *
 * @author lauri
 */
class LogicalNotGuard<R> implements Guard<R> {

    private Guard guard;

    public LogicalNotGuard(Guard guard) {
        this.guard = guard;
    }

    @Override
    public boolean allow(R runtime) {
        return !guard.allow(runtime);
    }
}
