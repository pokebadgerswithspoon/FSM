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
import java.util.stream.Collectors;

/**
 *
 * @author lauri
 */
public class StateHandler<S, E, R> {

    final Map<E, Deque<EventHandler<S, E, R, ?>>> eventMap = new HashMap<>();

    public <P> void register(E event, Action<R,P> action, Guard<R,P> guard, S stateTo) {
        Deque<EventHandler<S, E, R, ?>> handlers = handlers(event);
        handlers.addFirst(new EventHandler<>(action, guard, stateTo));
    }

    private Deque<EventHandler<S, E, R, ?>> handlers(E event) {
        Deque<EventHandler<S, E, R, ?>> eventHandlers = eventMap.get(event);
        if (eventHandlers == null) {
            eventHandlers = new LinkedList<>();
            eventMap.put(event, eventHandlers);
        }
        return eventHandlers;
    }

    /**
     * Handle event in given state
     *
     * @param state 
     * @param event 
     * @param runtime 
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
                a.execute(runtime, event.payload);
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

        final Action<R, P> action;
        final Guard<R, P> guard;
        final S stateTo;

        EventHandler(Action<R, P> action, Guard<R, P> guard, S stateTo) {
            this.action = action == null ? Action.TAKE_NO_ACTION : action;
            this.guard = guard == null ? Guard.ALLOW : guard;
            this.stateTo = stateTo;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("->")
                    .append(guard == Guard.ALLOW ? "": "?")
                    .append(action == Action.TAKE_NO_ACTION ? "": "a")
                    .append(stateTo)
                    .toString();
        }
    }
}
