/*
 * StateSyntax.java
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
import lombok.RequiredArgsConstructor;

import static fsm.Guard.and;

/**
 *
 * @author lauri
 */
public interface StateSyntax<S, E, R> {

    void to(S state2);

    void keepState();

    @RequiredArgsConstructor
    class Impl<S, E, R, P> implements StateSyntax<S, E, R> {

        private final  TransitionSyntax.Impl<S,E,R,P> transition;
        private final Action<R, P, S> action;

        @Override
        public void to(S state) {
            StateHandler<S, E, R> stateHandler = transition.handler;
            E event = transition.event;
            Guard<R, P> guard = transition.guards.isEmpty()
                    ? null
                    : and(transition.guards);
            stateHandler.register(event, action, guard, state);
        }

        @Override
        public void keepState() {
            to(null);
        }
    }

}
