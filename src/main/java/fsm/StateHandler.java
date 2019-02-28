/*
 * StateHandler.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lauri
 */
public class StateHandler<S, E, R> {

    Map<E, Collection<EventHandler<S, E, R>>> eventMap = new HashMap();

    public void register(E event, Action<S, E, R> action, Guard<S, E, R> guard, S stateTo) {
        Collection<EventHandler<S, E, R>> handlers = handlers(event);
        handlers.add(new EventHandler(action, guard, stateTo));
    }

    private Collection<EventHandler<S, E, R>> handlers(E event) {
        Collection<EventHandler<S, E, R>> result = eventMap.get(event);
        if (result == null) {
            result = new ArrayList();
            eventMap.put(event, result);
        }
        return result;
    }

    /**
     * Handle event in give state
     *
     * @param state
     * @param event
     * @param runtime
     * @return new state or null if no action taken
     */
    public S handle(S state, Event<E> event, R runtime) {
        Collection<EventHandler<S, E, R>> handlers = handlers(event.getType());
        boolean stateToFound = false;
        S stateTo = null;
        for (EventHandler<S, E, R> handler : handlers) {
            if (handler.guard.allow(event, state, runtime)) {
                if (!stateToFound) {
                    stateTo = handler.stateTo;
                    stateToFound = true;
                }
                if (handler.stateTo == stateTo) {
                    handler.action.execute(runtime, new TransitionContext.Impl<S, E>(state, stateTo, event));
                }
            }
        }
        if (stateToFound) {
            return stateTo == null ? state : stateTo;
        } else {
            return null;
        }
    }

    private static class EventHandler<S, E, R> {

        private Action<S, E, R> action;
        private Guard guard;
        private S stateTo;

        public EventHandler(Action action, Guard guard, S stateTo) {
            this.action = action == null ? Action.TAKE_NO_ACTION : action;
            this.guard = guard == null ? Guard.ALLOW : guard;
            this.stateTo = stateTo;
        }
    }
}
