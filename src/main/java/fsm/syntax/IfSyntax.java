/*
 * IfSyntax.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.syntax;

import fsm.Guard;

/**
 *
 * @author lauri
 */
public class IfSyntax {

    public interface Open {

        public IfSyntax.Close startIf(Guard guard);
    }

    public static interface Close {

        public TransitionSyntax elseif(Guard guard);

        public TransitionSyntax endIf();
    }
}
