/*
 * Event.java
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
public class Event<E, P> {

    public final E type;
    public final P payload;

    public Event(E type) {
        this(type, null);
    }

    public Event(E type, P payload) {
        this.type = type;
        this.payload = payload;
    }
}
