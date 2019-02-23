/*
 * Guards.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.util;

import fsm.Guard;
import java.util.Collection;

/**
 * Utility class with shortcuts to operations like AND and NOT
 *
 * @author lauri
 */
public class Guards {

    /**
     * @see LogicalAndGuard
     * @param guards to check
     * @return a guard which will be triggered if all guards in param agree to
     */
    public static Guard AND(Guard... guards) {
        return new LogicalAndGuard(guards);
    }

    /**
     * @see LogicalAndGuard
     * @param guards to check
     * @return a guard which will be triggered if all guards in param agree to
     */
    public static Guard AND(Collection<Guard> guards) {
        return new LogicalAndGuard(guards);
    }

    /**
     * @see LogicalNotGuard
     * @param guard
     * @return A guard that will trigger if guard in param is doesn't allow to
     */
    public static Guard NOT(Guard guard) {
        return new LogicalNotGuard(guard);
    }
}
