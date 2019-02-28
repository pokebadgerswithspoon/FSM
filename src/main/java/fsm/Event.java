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
public interface Event<E> {

    E getType();

    interface WithPayload<P> {

        public P getPayload();
    }

    public static class Impl<E, P> implements Event<E>, Event.WithPayload<P> {

        private final E type;
        private final P payload;

        public Impl(E type) {
            this(type, null);
        }

        public Impl(E type, P payload) {
            this.type = type;
            this.payload = payload;
        }

        @Override
        public E getType() {
            return type;
        }

        public P getPayload() {
            return payload;
        }
    }

}
