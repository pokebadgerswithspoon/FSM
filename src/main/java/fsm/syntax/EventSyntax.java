/*
 * EventSyntax.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm.syntax;

import fsm.Event;

/**
 *
 * @author lauri
 */
public interface EventSyntax {
    public TransitionSyntax on(Event.Type event);
}
