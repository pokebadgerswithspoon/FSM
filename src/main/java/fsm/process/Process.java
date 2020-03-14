package fsm.process;

import fsm.FsmDefinition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface Process<S> {

    S getStart();

    S getEnd();

    FsmDefinition getFsmDefinition();

}
