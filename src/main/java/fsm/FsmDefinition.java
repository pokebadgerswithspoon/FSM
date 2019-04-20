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
public class FsmDefinition<S, E> implements FsmDefinitionSyntax<S, E> {

    private final StateHandler<S, E> anyStateHandler = new StateHandler();
    private final Map<S, StateHandler<S, E>> stateHandlers;

    public FsmDefinition() {
        stateHandlers = new HashMap();
    }

    @Override
    public EventSyntax<S, E> in(S state) {
        StateHandler handler = handler(state);
        return new EventSyntax.Impl(handler);
    }

    @Override
    public TransitionSyntax<S, E> on(E event) {
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

    public Fsm<S, E> define(S initialState) {
        FsmImpl fsm = new FsmImpl(initialState);
        if (!stateHandlers.containsKey(initialState)) {
            throw new IllegalArgumentException("State " + initialState + " is unknown to FSM definition");
        }
        return fsm;
    }

    private class FsmImpl implements Fsm<S, E> {

        private S currentState;

        public FsmImpl(S initialState) {
            this.currentState = initialState;
        }

        @Override
        public S getState() {
            return currentState;
        }

        @Override
        public void handle(Event<E,Object> event) {
            Iterable<StateHandler<S, E>> handlers = iterableNonNulls(anyStateHandler, stateHandlers.get(currentState));
            
            for(StateHandler<S, E> handler: handlers) {
                S newState = handler.handle(currentState, event);
                if (!currentState.equals(newState)) {
                    currentState = newState;
                    return;
                }
            }
        }
    }
}
