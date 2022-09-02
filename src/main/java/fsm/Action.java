/*
 * Action.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

/**
 * Interface to actions taken on state transition in FSM
 *
 * @author lauri
 */
public interface Action<R, P, S> {

    /**
     * Action taken on transition. Executed even if FSM definition asked to keep
     * it's state
     *
     * @param runtime can be null
     * @param transition of event that triggered the transition
     */
    void execute(R runtime, Transition<S, P, ?> transition);

    /**
     * An action that does nothing
     */
    Action NOOP = (runtime, payload) -> {};

    /**
     * Take no action
     * @param <R> runtime
     * @param <P> payload
     * @return an action that does nothing
     */
    static <R, P, S> Action<R, P, S> noop() {
        return (r, p) -> {};
    }

    static <R, P, S> Action<R, P, S> combine(Action<R, P, S>... actions) {
        return (runtime, transition) -> {
            for (Action<R, P, S> action : actions) {
                action.execute(runtime, transition);
            }
        };
    }
}
