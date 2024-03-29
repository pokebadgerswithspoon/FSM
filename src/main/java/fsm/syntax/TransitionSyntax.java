/*
 * TransitionSyntax.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.syntax;

import fsm.Action;
import fsm.Guard;
import fsm.StateHandler;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author lauri
 */
public interface TransitionSyntax<S, E, R, P> {

    StateSyntax<S, E, R> transition();

    StateSyntax<S, E, R> transition(Action<R, P, S> action);

    TransitionSyntax<S, E, R, P> onlyIf(Guard<R, P> guard);

    class Impl<S, E, R, P> implements TransitionSyntax<S, E, R, P> {

        final StateHandler<S,E,R> handler;
        final E event;
        final List<Guard<R, P>> guards = new LinkedList<>();

        public Impl(final StateHandler<S,E,R> handler, final E event) {
            this.handler = handler;
            this.event = event;
        }

        @Override
        public StateSyntax<S, E, R> transition() {
            return transition(Action.NOOP);
        }

        @Override
        public StateSyntax<S, E, R> transition(Action<R, P, S> action) {
            return new StateSyntax.Impl<>(this, action);
        }

        @Override
        public TransitionSyntax<S,E,R,P> onlyIf(Guard<R, P> guard) {
            if(guard == null) {
                return this;
            }
            guards.add(guard);
            return this;
        }
    }
}
