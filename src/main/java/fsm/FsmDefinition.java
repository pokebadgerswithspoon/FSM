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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lauri
 */
public class FsmDefinition<S, E> implements FsmDefinitionSyntax<S, E> {

    private Map<S, StateHandler<S, E>> stateHandlers;

    public FsmDefinition() {
        stateHandlers = new HashMap();
    }

    @Override
    public EventSyntax in(S state) {
        StateHandler handler = handler(state);
        return new EventSyntax.Impl(handler);
    }

    private StateHandler handler(S state) {
        StateHandler result = stateHandlers.get(state);
        if (result == null) {
            result = new StateHandler();
            stateHandlers.put(state, result);
        }
        return result;
    }

    public Fsm define(final FsmRuntime runtime, S initialState) {
        FsmImpl fsm = new FsmImpl(runtime, initialState);
        if (!stateHandlers.containsKey(initialState)) {
            throw new IllegalArgumentException("State " + initialState + " is unknown to FSM definition");
        }
        return fsm;
    }

    private class FsmImpl implements Fsm<S, E> {

        private final FsmRuntime runtime;
        private S currentState;

        public FsmImpl(FsmRuntime runtime, S initialState) {
            this.runtime = runtime;
            this.currentState = initialState;
        }

        @Override
        public S getState() {
            return currentState;
        }

        @Override
        public void handle(Event<E> event) {
            S state = getState();
            StateHandler<S, E> handler = stateHandlers.get(state);
            if (handler == null) {
                throw new IllegalStateException("Uknown state");
            } else {
                S newState = handler.handle(state, event, runtime);
                if (newState != null) {
                    currentState = newState;
                }
            }
        }
    }
}
