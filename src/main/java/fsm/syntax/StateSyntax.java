/*
 * StateSyntax.java
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
import fsm.util.Guards;
import java.util.Collection;

/**
 *
 * @author lauri
 */
public interface StateSyntax<S, E, R> {

    public void to(S state2);

    public void keepState();

    static class Impl<S, E, R> implements StateSyntax<S, E, R> {

        private TransitionSyntax.Impl<S,E,R> transtition;
        private Action action;

        public Impl(final TransitionSyntax.Impl transition, final Action action) {
            this.transtition = transition;
            this.action = action;
        }

        @Override
        public void to(S state) {
            StateHandler<S, E, R> stateHandler = transtition.handler;
            E event = transtition.event;
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

}
