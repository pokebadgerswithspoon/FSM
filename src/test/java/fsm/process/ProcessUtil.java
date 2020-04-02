package fsm.process;

import fsm.Fsm;
import fsm.FsmDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class ProcessUtil {
    static void run(Process process) {
        FsmDefinition def = process.getFsmDefinition();
        Object end = process.getEnd();
        Object start = process.getStart();
        Fsm fsm = def.define(null, start);

        runFsm(def, fsm);
        if (!end.equals(fsm.getState())) {
            throw new ProcessDidNotEndException("ProcessBuilder did not end well");
        }
    }

    static void run(FsmDefinition def) {
        runFsm(def, def.define(null, 0));
    }

    static void runFsm(FsmDefinition def, final Fsm fsm) {
        log.info("FSM is {}", fsm);

        Function<Object, Optional<String>> whatEvent = (state) -> Stream.of("THEN", "TIMEOUT", "HELLO", "EVENT").filter((e) -> def.hasHandler(state, e)).findFirst();

        int i = 0;
        for (Optional e = whatEvent.apply(fsm.getState()); e.isPresent(); ) {
            Object event = e.get();
            Object state = fsm.getState();
            fsm.handle(event);
            log.info("State {}, after [{}] is {}", state, event, fsm.getState());
            if (i++ > 10) {
                throw new IllegalStateException("Too much steps");
            }
            e = whatEvent.apply(fsm.getState());
        }
    }
}
