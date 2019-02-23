/*
 * Fsm.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

/**
 *
 * @author lauri
 */
public interface Fsm<S,E> {

    /**
     * Make machine to handle an event, this may trigger state change
     *
     * @param event to handle
     */
    void handle(Event<E> event);

    /**
     * State of the machine
     *
     * @return never null value
     */
    S getState();
    
    
}
