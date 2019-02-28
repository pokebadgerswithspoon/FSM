/*
 * Guard.java
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
public interface Guard<S, E, R> {

    public boolean allow(Event<E> event, S state, R runtime);
    public static final Guard ALLOW = new Guard() {
        @Override
        public boolean allow(Event event, Object state, Object runtime) {
            return true;
        }
    };
}
