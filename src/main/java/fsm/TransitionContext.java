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
public interface TransitionContext {

    public State getFrom();

    public State getTo();

    public Event getEvent();

    static class Impl implements TransitionContext {

        private State from, to;
        private Event event;

        public Impl(State from, State to, Event event) {
            this.from = from;
            // keep state
            this.to = to == null ? from : to;
            this.event = event;
        }

        @Override
        public State getFrom() {
            return from;
        }

        @Override
        public State getTo() {
            return to;
        }

        @Override
        public Event getEvent() {
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
