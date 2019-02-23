/*
 * TransitionSyntaxImpl.java
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
import fsm.syntax.StateSyntax;
import fsm.syntax.TransitionSyntax;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author lauri
 */
class TransitionSyntaxImpl implements TransitionSyntax {

    StateHandler handler;
    Event.Type event;
    Collection<Guard> guards = null;

    public TransitionSyntaxImpl(final StateHandler handler, final Event.Type event) {
        this.handler = handler;
        this.event = event;
    }

    @Override
    public StateSyntax transition(Action action) {
        return new StateSyntaxImpl(this, action);
    }

    @Override
    public TransitionSyntax onlyIf(Guard guard) {
        if (guards == null && guard != null) {
            guards = new ArrayList();
            guards.add(guard);
        }
        return this;
    }
}
