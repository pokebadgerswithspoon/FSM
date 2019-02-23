/*
 * EventSyntaxImpl.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.impl;

import fsm.Event;
import fsm.syntax.EventSyntax;
import fsm.syntax.TransitionSyntax;

/**
 *
 * @author lauri
 */
public class EventSyntaxImpl implements EventSyntax {

    private StateHandler handler;

    public EventSyntaxImpl(final StateHandler handler) {
        this.handler = handler;
    }

    @Override
    public TransitionSyntax on(Event.Type event) {
        return new TransitionSyntaxImpl(handler, event);
    }
}
