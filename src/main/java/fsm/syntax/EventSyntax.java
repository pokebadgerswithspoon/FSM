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

/**
 *
 * @author lauri
 */
public interface EventSyntax<S, E> {

    public TransitionSyntax<S, E> on(E event);

    static class Impl<S, E> implements EventSyntax<S, E> {

        private StateHandler handler;

        public Impl(final StateHandler handler) {
            this.handler = handler;
        }

        @Override
        public TransitionSyntax on(E event) {
            return new TransitionSyntax.Impl(handler, event);
        }
    }
}
