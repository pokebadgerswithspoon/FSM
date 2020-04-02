package fsm.process.impl;

import fsm.FsmDefinition;
import fsm.process.Process;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
class ProcessImpl<S,E,R> implements Process<S,E,R> {

    private final S start;
    private final S end;
    private final FsmDefinition<S,E,R> fsmDefinition;
}
