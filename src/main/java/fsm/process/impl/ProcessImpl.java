package fsm.process.impl;

import fsm.FsmDefinition;
import fsm.process.Process;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
class ProcessImpl implements Process {

    private final Ref startRef;
    private final Ref endRef;
    private final FsmDefinition fsmDefinition;
}
