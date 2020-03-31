package fsm.process;

import fsm.FsmDefinition;

public interface Process<S> {

    S getStart();

    S getEnd();

    FsmDefinition getFsmDefinition();

}
