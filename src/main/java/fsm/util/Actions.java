/*
 * Actions.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import fsm.Action;

/**
 * Utility class with shortcuts to some common actions
 *
 * @see ChainAction
 * @author lauri
 */
public class Actions {

    /**
     * Returns an action which will execute all the bundled actions
     *
     * @see ChainAction
     * @param actions bundled
     * @return a chain of actions
     */
    public static Action chain(Action... actions) {
        return new ChainAction(actions);
    }
}
