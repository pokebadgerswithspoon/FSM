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
public class FsmDefinition implements FsmDefinitionSyntax {

    private Map<State, StateHandler> stateHandlers;

    public FsmDefinition() {
        stateHandlers = new HashMap();
    }

    @Override
    public EventSyntax in(State state) {
        StateHandler handler = handler(state);
        return new EventSyntax.Impl(handler);
    }

    private StateHandler handler(State state) {
        StateHandler result = stateHandlers.get(state);
        if (result == null) {
            result = new StateHandler();
            stateHandlers.put(state, result);
        }
        return result;
    }

    public Fsm define(final FsmRuntime runtime, State initialState) {
        FsmImpl fsm = new FsmImpl(runtime, initialState);
        if (!stateHandlers.containsKey(initialState)) {
            throw new IllegalArgumentException("State " + initialState + " is unknown to FSM definition");
        }
        return fsm;
    }

    private class FsmImpl implements Fsm {

        private final FsmRuntime runtime;
        private State currentState;

        public FsmImpl(FsmRuntime runtime, State initialState) {
            this.runtime = runtime;
            this.currentState = initialState;
        }

        @Override
        public State getState() {
            return currentState;
        }

        @Override
        public void handle(Event event) {
            State state = getState();
            StateHandler handler = stateHandlers.get(state);
            if (handler == null) {
                throw new IllegalStateException("Uknown state");
            } else {
                State newState = handler.handle(state, event, runtime);
                if (newState != null) {
                    currentState = newState;
                }
            }
        }
    }
}
