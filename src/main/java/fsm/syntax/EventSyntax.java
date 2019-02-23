/*
 * EventSyntax.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.syntax;

import fsm.StateHandler;
import fsm.Event;

/**
 *
 * @author lauri
 */
public interface EventSyntax {

    public TransitionSyntax on(Event.Type event);

    static class Impl implements EventSyntax {

        private StateHandler handler;

        public Impl(final StateHandler handler) {
            this.handler = handler;
        }

        @Override
        public TransitionSyntax on(Event.Type event) {
            return new TransitionSyntax.Impl(handler, event);
        }
    }
}
