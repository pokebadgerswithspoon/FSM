/*
 * StateHandler.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 *
 * @author lauri
 */
public class StateHandler<S, E, R> {

    final Map<E, LinkedList<EventHandler<S, E, R, ?>>> eventMap = new HashMap<>();
    private final BiConsumer<LinkedList<EventHandler<S, E, R, ?>>, EventHandler<S,E,R,?>> addHandler;

    StateHandler(FsmDefinition.ExecutionOrder order) {
        switch (order) {
            case LAST_TO_FIRST:
                addHandler = (h, e) -> h.addFirst(e);
                break;
            case FIRST_TO_LAST:
                addHandler = (h, e) -> h.add(e);
                break;
            default:
                throw new IllegalArgumentException("Unkown processing order setup");
        }
    }

    public <P> void register(E event, Action<R,P,S> action, Guard<R,P> guard, S stateTo) {
        addHandler.accept(
                handlers(event),
                new EventHandler<>(action, guard, stateTo)
        );
    }

    private LinkedList<EventHandler<S, E, R, ?>> handlers(E event) {
        LinkedList<EventHandler<S, E, R, ?>> eventHandlers = eventMap.get(event);
        if (eventHandlers == null) {
            eventHandlers = new LinkedList<>();
            eventMap.put(event, eventHandlers);
        }
        return eventHandlers;
    }

    /**
     * Handle event in given state
     *
     * @param state the machine is in
     * @param event with payload
     * @param runtime execution runtime
     * @return new state or null if no action taken
     */
    public <P> S handle(S state, Event<E,P> event, R runtime) {
        Collection<EventHandler<S, E, R, ?>> handlers = handlers(event.type);
        S stateTo = null;
        for (EventHandler<S, E, R, ?> handler : handlers) {
            Guard g = handler.guard;
            if (g.allow(runtime, event.payload)) {
                Action a = handler.action;
                stateTo = handler.stateTo;
                a.execute(runtime, new Transition.Impl(state, stateTo, event.type, event.payload));
                break;
            }
        }
        return stateTo == null ? state : stateTo;
    }

    public boolean knowsHowToHandle(E event) {
        return eventMap.containsKey(event) && !eventMap.get(event).isEmpty();
    }

    @Override
    public String toString() {
        return
                eventMap.entrySet()
                        .stream()
                        .map(e -> "on \""+e.getKey() +"\" "+ e.getValue().toString())
                        .collect(Collectors.joining(",")
                        );
    }

    private static class EventHandler<S, E, R, P> {

        final Action<R, P, S> action;
        final Guard<R, P> guard;
        final S stateTo;

        EventHandler(Action<R, P, S> action, Guard<R, P> guard, S stateTo) {
            this.action = action == null ? Action.NOOP : action;
            this.guard = guard == null ? Guard.ALLOW : guard;
            this.stateTo = stateTo;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("->")
                    .append(guard == Guard.ALLOW ? "": "?")
                    .append(action == Action.NOOP ? "": "a")
                    .append(stateTo)
                    .toString();
        }
    }
}
