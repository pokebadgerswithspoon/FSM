package fsm.process;

import fsm.FsmDefinition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface Process {

    Ref getStartRef();

    Ref getEndRef();

    FsmDefinition getFsmDefinition();

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    @ToString
    class Ref<T> {
        public T state;
        private final String name;

        Ref() {
            this("NONE");
        }
    }
}
