/*
 * Event.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import fsm.syntax.EventSyntax;
import fsm.syntax.FsmDefinitionSyntax;
import fsm.syntax.TransitionSyntax;
import static fsm.util.Util.iterableNonNulls;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lauri
 */
public class FsmDefinition<S, E, R> implements FsmDefinitionSyntax<S, E, R> {

    private final StateHandler<S, E, R> anyStateHandler = new StateHandler();
    private final Map<S, StateHandler<S, E, R>> stateHandlers;

    public FsmDefinition() {
        stateHandlers = new HashMap();
    }

    @Override
    public EventSyntax<S, E, R> in(S state) {
        StateHandler handler = handler(state);
        return new EventSyntax.Impl(handler);
    }

    @Override
    public TransitionSyntax<S, E, R> on(E event) {
        return new TransitionSyntax.Impl(anyStateHandler, event);
    }

    private StateHandler handler(S state) {
        StateHandler result = stateHandlers.get(state);
        if (result == null) {
            result = new StateHandler();
            stateHandlers.put(state, result);
        }
        return result;
    }

    public Fsm<S, E, R> define(final R runtime, S initialState) {
        FsmImpl fsm = new FsmImpl(runtime, initialState);
        if (!stateHandlers.containsKey(initialState)) {
            throw new IllegalArgumentException("State " + initialState + " is unknown to FSM definition");
        }
        return fsm;
    }

    private class FsmImpl implements Fsm<S, E, R> {

        private final R runtime;
        private S currentState;

        public FsmImpl(R runtime, S initialState) {
            this.runtime = runtime;
            this.currentState = initialState;
        }

        @Override
        public S getState() {
            return currentState;
        }

        @Override
        public void handle(E event) {
            handle(new Event(event));
        }
        @Override
        public <P> void handle(E event, P payload) {
            handle(new Event(event, payload));
        }

        void handle(Event<E,Object> e) {
            Iterable<StateHandler<S, E, R>> handlers = iterableNonNulls(anyStateHandler, stateHandlers.get(currentState));
            for(StateHandler<S, E, R> handler: handlers) {
                S newState = handler.handle(currentState, e, runtime);
                if (!currentState.equals(newState)) {
                    currentState = newState;
                    return;
                }
            }
        }


        public R getRuntime() {
            return runtime;
        }
    }
}
