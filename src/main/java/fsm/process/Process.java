package fsm.process;

import fsm.FsmDefinition;

public interface Process<S,E,R> {

    S getStart();

    S getEnd();

    FsmDefinition<S,E,R> getFsmDefinition();

}
