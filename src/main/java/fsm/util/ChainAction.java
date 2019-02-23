/*
 * ChainAction.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import fsm.Action;
import fsm.FsmRuntime;
import fsm.TransitionContext;

/**
 * A chain of actions to execute
 *
 * @author lauri
 */
class ChainAction implements Action {

    private final Action[] chain;

    public ChainAction(Action... action) {
        this.chain = action;
    }

    @Override
    public void execute(FsmRuntime runtime, TransitionContext transition) {
        for (Action action : chain) {
            action.execute(runtime, transition);
        }
    }
}
