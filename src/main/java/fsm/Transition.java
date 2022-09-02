/*
 * State.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author lauri
 */
public interface Transition<S,E,P> {

    S getFrom();

    S getTo();

    E getEvent();

    P getPayload();

    @RequiredArgsConstructor
    @Getter
    class Impl<S,E,P> implements Transition<S,E,P> {

        private final S from, to;
        private final E event;
        private final P payload;
    }
}
