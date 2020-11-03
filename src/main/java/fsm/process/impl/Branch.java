package fsm.process.impl;

import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Branch<S,E,R> {
    final Ref<S> startRef;
    final Ref<S> endRef;
    final ProcessBuilderImpl<S,E,R> processBuilder;

}
