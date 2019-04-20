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
public interface Guard<R> {

    public boolean allow(R runtime);
    
    public static final Guard ALLOW = new Guard() {
        @Override
        public boolean allow(Object runtime) {
            return true;
        }
    };

    public static Guard not(Guard guard) {
        return new AbstractWrap(guard) {
            @Override
            public boolean allow(Object runtime) {
                return !guard.allow(runtime);
            }
        };
    }

    static abstract class AbstractWrap<R> implements Guard<R> {

        protected final Guard<R> guard;

        public AbstractWrap(Guard<R> guard) {
            this.guard = guard;
        }
    }

}
