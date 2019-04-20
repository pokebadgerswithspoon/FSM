/*
 * TransitionSyntax.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.syntax;

import fsm.StateHandler;
import fsm.Action;
import fsm.Guard;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author lauri
 */
public interface TransitionSyntax<S, E> {

    public StateSyntax<S, E> transition();

    public StateSyntax<S, E> transition(Action action);

    public TransitionSyntax<S, E> onlyIf(Guard guard);

    static class Impl<S, E> implements TransitionSyntax<S, E> {

        StateHandler handler;
        E event;
        Collection<Guard> guards = null;

        public Impl(final StateHandler handler, final E event) {
            this.handler = handler;
            this.event = event;
        }

        @Override
        public StateSyntax<S, E> transition() {
            return transition(Action.TAKE_NO_ACTION);
        }

        @Override
        public StateSyntax<S, E> transition(Action action) {
            return new StateSyntax.Impl(this, action);
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
}
