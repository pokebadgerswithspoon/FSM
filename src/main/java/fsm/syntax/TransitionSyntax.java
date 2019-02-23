/*
 * TransitionSyntax.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.syntax;

import fsm.Action;
import fsm.Guard;

/**
 *
 * @author lauri
 */
public interface TransitionSyntax {

    public StateSyntax transition(Action action);

    public TransitionSyntax onlyIf(Guard guard);
}
