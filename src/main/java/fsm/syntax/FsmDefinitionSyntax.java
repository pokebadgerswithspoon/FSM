/*
 * FsmDefinitionSyntax.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.syntax;

import fsm.State;

/**
 *
 * @author lauri
 */
public interface FsmDefinitionSyntax {

    public EventSyntax in(State state);
}
