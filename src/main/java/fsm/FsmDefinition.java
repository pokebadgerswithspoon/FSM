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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static fsm.util.Util.iterableNonNulls;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author lauri
 */
public class FsmDefinition<S, E, R> implements FsmDefinitionSyntax<S, E, R> {

    private final StateHandler<S, E, R> anyStateHandler;
    private final Map<S, StateHandler<S, E, R>> stateHandlers = new HashMap<>();
    private final ExecutionOrder executionOrder;

    public FsmDefinition() {
        this(ExecutionOrder.LAST_TO_FIRST);
    }
    public FsmDefinition(ExecutionOrder executionOrder) {
        requireNonNull(executionOrder);
        this.executionOrder = executionOrder;
        this.anyStateHandler = new StateHandler<>(executionOrder);
    }


    @Override
    public EventSyntax<S, E, R> in(S state) {
        StateHandler<S,E,R> handler = handler(state);
        return new EventSyntax.Impl<>(handler);
    }

    public void registerState(S state) {
        handler(state);
    }

    @Override
    public <P> TransitionSyntax<S, E, R, P> on(E event, Class<P> payloadClass) {
        return new TransitionSyntax.Impl<>(anyStateHandler, event);
    }

    private StateHandler<S,E,R> handler(S state) {
        StateHandler<S,E,R> result = stateHandlers.get(state);
        if (result == null) {
            result = new StateHandler<>(executionOrder);
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

    public boolean hasHandler(S state, E event) {
        return ofNullable(state)
                .map(stateHandlers::get)
                .flatMap(h ->
                        ofNullable(event)
                                .map(h::knowsHowToHandle)
                )
                .orElse(false);
    }

    public Set<S> states() {
        return stateHandlers.keySet();
    }

    public enum ExecutionOrder {
        LAST_TO_FIRST,
        FIRST_TO_LAST;
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
        public Optional<Transition<S, E, ?>> handle(E event) {
            return handle(new Event(event));
        }

        @Override
        public <P> Optional<Transition<S, E, P>> handle(E event, P payload) {
            return handle(new Event<>(event, payload));
        }

        <P> Optional<Transition<S, E, P>> handle(Event<E, P> e) {
            Iterable<StateHandler<S, E, R>> handlers = iterableNonNulls(stateHandlers.get(currentState), anyStateHandler);
            for (StateHandler<S, E, R> handler : handlers) {
                Optional<Transition<S, E, P>> transition = handler.handle(currentState, e, runtime);
                if(transition.isPresent()) {
                    transition.map(Transition::getTo)
                            .filter(Objects::nonNull)
                            .filter(not(currentState::equals))
                            .ifPresent(
                                    newState -> currentState = newState
                            );
                    return transition;
                }
            }
            return Optional.empty();
        }

        private Predicate<S> not(Predicate<S> predicate) {
            return t -> !predicate.test(t);
        }


        public R getRuntime() {
            return runtime;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Current state: ").append(currentState).append("\n");
            stateHandlers.entrySet()
                    .forEach(e -> b.append("State "+e.getKey()+" events:").append(" {").append(e.getValue().toString()).append("}\n"));

            return b.toString();
        }
    }
}
