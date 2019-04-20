/*
 * State.java
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
public interface TransitionContext<S,E> {

    public S getFrom();

    public S getTo();

    public Event<E,?> getEvent();

    static class Impl<S,E> implements TransitionContext<S,E> {

        private S from, to;
        private Event<E,?> event;

        public Impl(S from, S to, Event<E,?> event) {
            this.from = from;
            // keep state
            this.to = to == null ? from : to;
            this.event = event;
        }

        @Override
        public S getFrom() {
            return from;
        }

        @Override
        public S getTo() {
            return to;
        }

        @Override
        public Event<E,?> getEvent() {
            return event;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("{from: ").append(from)
                    .append(",to: ").append(to)
                    .append(",event: ").append(event)
                    .append("}")
                    .toString();
        }
    }
}
