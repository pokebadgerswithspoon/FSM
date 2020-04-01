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

import static fsm.Guard.and;

/**
 *
 * @author lauri
 */
public interface StateSyntax<S, E, R> {

    void to(S state2);

    void keepState();

    class Impl<S, E, R, P> implements StateSyntax<S, E, R> {

        private TransitionSyntax.Impl<S,E,R,P> transition;
        private Action<R, P> action;

        public Impl(final TransitionSyntax.Impl<S,E,R,P> transition, final Action<R, P> action) {
            this.transition = transition;
            this.action = action;
        }

        @Override
        public void to(S state) {
            StateHandler<S, E, R> stateHandler = transition.handler;
            E event = transition.event;
            Guard<R, P> guard = transition.guards == null
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
