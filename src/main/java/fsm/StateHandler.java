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
public class StateHandler {

    Map<Event.Type, Collection<EventHandler>> eventMap = new HashMap();

    public void register(Event.Type event, Action action, Guard guard, State stateTo) {
        Collection<EventHandler> handlers = handlers(event);
        handlers.add(new EventHandler(action, guard, stateTo));
    }

    private Collection<EventHandler> handlers(Event.Type event) {
        Collection<EventHandler> result = eventMap.get(event);
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
    public State handle(State state, Event event, FsmRuntime runtime) {
        Collection<EventHandler> handlers = handlers(event.getType());
        boolean stateToFound = false;
        State stateTo = null;
        for (EventHandler handler : handlers) {
            if (handler.guard.allow(event, state, runtime)) {
                if (!stateToFound) {
                    stateTo = handler.stateTo;
                    stateToFound = true;
                }
                if (handler.stateTo == stateTo) {
                    handler.action.execute(runtime, new TransitionContext.Impl(state, stateTo, event));
                }
            }
        }
        if (stateToFound) {
            return stateTo == null ? state : stateTo;
        } else {
            return null;
        }
    }

    private static class EventHandler {

        private Action action;
        private Guard guard;
        private State stateTo;

        public EventHandler(Action action, Guard guard, State stateTo) {
            this.action = action == null ? Action.TAKE_NO_ACTION : action;
            this.guard = guard == null ? Guard.ALLOW : guard;
            this.stateTo = stateTo;
        }
    }
}
