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
public interface Action<R> {

    /**
     * Action taken on transition. Executed even if FSM definition asked to keep
     * it's state
     *
     * @param runtime can be null
     * @param transition never null
     */
    public void execute(R runtime);
    
    public static final Action TAKE_NO_ACTION = new Action() {
        @Override
        public void execute(Object runtime) {
        }
    };
    
    public static <R> Action<R> combine(Action<R> ...actions) {
        return (R runtime) -> {
            for(Action<R> action: actions) {
                action.execute(runtime);
            }
        };
    }
    
}
