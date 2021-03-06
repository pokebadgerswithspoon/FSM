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
import lombok.RequiredArgsConstructor;

/**
 *
 * @author lauri
 */
public interface EventSyntax<S, E, R> {

    default TransitionSyntax<S, E, R, ?> on(E event) {
        return on(event, Object.class);
    }

    <P> TransitionSyntax<S, E, R, P> on(E event, Class<P> payload);

    @RequiredArgsConstructor
    class Impl<S, E, R> implements EventSyntax<S, E, R> {

        private final StateHandler<S,E,R> handler;

        @Override
        public <P> TransitionSyntax<S,E,R,P> on(E event, Class<P> payloadClass) {
            return new TransitionSyntax.Impl<>(handler, event);
        }
    }
}
