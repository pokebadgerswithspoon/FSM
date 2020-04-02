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
public interface Action<R, P> {

    /**
     * Action taken on transition. Executed even if FSM definition asked to keep
     * it's state
     *
     * @param runtime can be null
     * @param payload of event that triggered the transition
     */
    void execute(R runtime, P payload);

    Action TAKE_NO_ACTION = (runtime, payload) -> {};

    static <R, P> Action<R, P> combine(Action<R, P>... actions) {
        return (R runtime, P payload) -> {
            for (Action<R, P> action : actions) {
                action.execute(runtime, payload);
            }
        };
    }
}
