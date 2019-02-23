/*
 * StateSyntaxImpl.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.impl;


import fsm.Action;
import fsm.Event;
import fsm.Guard;
import fsm.State;
import fsm.syntax.StateSyntax;
import fsm.util.Guards;
import java.util.Collection;

/**
 *
 * @author lauri
 */
class StateSyntaxImpl implements StateSyntax {

    private TransitionSyntaxImpl transtition;
    private Action action;

    public StateSyntaxImpl(final TransitionSyntaxImpl transition, final Action action) {
        this.transtition = transition;
        this.action = action;
    }

    @Override
    public void to(State state) {
        StateHandler stateHandler = transtition.handler;
        Event.Type event = transtition.event;
        Guard guard = guard(transtition.guards);
        stateHandler.register(event, action, guard, state);
    }

    @Override
    public void keepState() {
        to(null);
    }

    private Guard guard(Collection<Guard> guards) {
        if (guards == null || guards.isEmpty()) {
            return null;
        } else if (guards.size() == 1) {
            return guards.iterator().next();
        } else {
            return Guards.AND(guards);
        }
    }
}
