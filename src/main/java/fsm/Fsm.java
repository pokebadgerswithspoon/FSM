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
public interface Fsm<S, E, R> {

    /**
     * Make machine to handle an event, this may trigger state change
     *
     * @param event to handle
     */
    <P> void handle(E event, P payload);
    public void handle(E event);

    /**
     * State of the machine
     *
     * @return never null value
     */
    S getState();

    R getRuntime();

}
