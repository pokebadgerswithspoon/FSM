/*
 * StateHandler.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author lauri
 */
public class StateHandler<S, E> {

    Map<E, Deque<EventHandler<S>>> eventMap = new HashMap();

    public void register(E event, Action action, Guard guard, S stateTo) {
        Deque<EventHandler<S>> handlers = handlers(event);
        handlers.addFirst(new EventHandler(action, guard, stateTo));
    }

    private Deque<EventHandler<S>> handlers(E event) {
        Deque<EventHandler<S>> result = eventMap.get(event);
        if (result == null) {
            result = new LinkedList<>();
            eventMap.put(event, result);
        }
        return result;
    }

    /**
     * Handle event in given state
     *
     * @param state 
     * @param event 
     * @return new state or null if no action taken
     */
    public S handle(S state, Event<E,Object> event) {
        Collection<EventHandler<S>> handlers = handlers(event.type);
        S stateTo = null;
        for (EventHandler<S> handler : handlers) {
            if (handler.guard.allow(event.payload)) {
                stateTo = handler.stateTo;
                handler.action.execute(event.payload);
                break;
            }
        }
        return stateTo == null ? state : stateTo;
    }

    private static class EventHandler<S> {

        Action action;
        Guard guard;
        S stateTo;

        EventHandler(Action action, Guard guard, S stateTo) {
            this.action = action == null ? Action.TAKE_NO_ACTION : action;
            this.guard = guard == null ? Guard.ALLOW : guard;
            this.stateTo = stateTo;
        }
    }
}
