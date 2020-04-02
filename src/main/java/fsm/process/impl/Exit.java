package fsm.process.impl;

import fsm.Guard;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Exit<S, E, R> {
    final Ref<S> refTo;
    final E event;
    final Guard<R, Object> guard;
}
