package fsm.process.impl;

import fsm.Guard;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Exit<S> {
    final Ref<S> refTo;
    final Object event;
    final Guard guard;
}
