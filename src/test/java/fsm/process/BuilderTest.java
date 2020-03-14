package fsm.process;

import fsm.Action;
import fsm.Fsm;
import fsm.FsmDefinition;
import fsm.Guard;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static fsm.process.ChooseSyntax.choose;
import static fsm.process.StayUntil.stayUntil;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class BuilderTest {
    @Mock
    Action A;
    @Mock
    Action B;
    Action logE = (r, p) -> log.info("Event");


    @Test
    public void emptyExample() {
        Process process = ProcessBuilder.builder()
            .start()
            .end();

        FsmDefinition def = process.getFsmDefinition();
        log(process);
        assertEquals(2, def.states().size());

        run(process);
    }

    private void log(FsmDefinition def) {
        log.info("State is:{}", def.define(null, 0));
    }

    @Test
    public void plainExample() {
        FsmDefinition def = ProcessBuilder.builder()
            .start()
            .then(A)
            .end()
            .getFsmDefinition();
        assertEquals(3, def.states().size());

        log(def);
        run(def);
        verify(A, times(1)).execute(any(), any());
    }

    @Test
    public void loopExample() {
        Ref refA = new Ref();
        ProcessBuilder.builder()
            .start()
            .then(A, refA)
            .then(B)
            .go(refA);
    }

    @Test
    public void eventExample() {
        Ref refB = new Ref("Ref B");
        Process process = ProcessBuilder.builder()
            .start()
            .then(A)
            .stay(
                stayUntil()
                    .on("event", refB)
            )
            .add(refB)
            .end();

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(4, fsmDefinition.states().size());

        run(process);
        verify(A, times(1)).execute(any(), any());
    }

    @Test
    public void eventsExample() {
        Process process = ProcessBuilder.builder()
            .start()
            .then(A)
            .stay(
                stayUntil()
                    .on("timeout", (b) -> b.end())
                    .on("hello", (b) -> b.then(B).end())
            )
            .add(new Ref())
            .end();

        FsmDefinition fsmDefinition = process.getFsmDefinition();

        log(process);
        assertEquals(7, fsmDefinition.states().size());

        run(fsmDefinition);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(0)).execute(any(), any());
    }


    @Test
    public void chooseExample() {
        Ref refE = new Ref();
        Process process = ProcessBuilder.builder()
            .start()
            .then(A)
            .choose(
                choose()
                    .when(Guard.ALLOW, (b) -> b.then(B).go(refE))
                    .otherwise((b) -> b.go(refE))
            )
            .add(refE)
            .end();

        log(process);

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(5, fsmDefinition.states().size());

        run(process);
        verify(B, times(1)).execute(any(), any());
    }


    private void run(Process process) {
        FsmDefinition def = process.getFsmDefinition();
        Object end = process.getEnd();
        Object start = process.getStart();
        Fsm fsm = def.define(null, start);

        runFsm(def, fsm);
        if (!end.equals(fsm.getState())) {
            throw new ProcessDidNotEndException("ProcessBuilder did not end well");
        }
    }

    private void run(FsmDefinition def) {
        runFsm(def, def.define(null, 0));
    }

    private void runFsm(FsmDefinition def, final Fsm fsm) {
        log.info("FSM is {}", fsm);

        Function<Object, Optional<String>> whatEvent = (state) -> Stream.of("then", "timeout", "hello", "event").filter((e) -> def.hasHandler(state, e)).findFirst();

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


    private void log(Process process) {
        log(process.getFsmDefinition());
        log.info("add: {}, end: {}", process.getStart(), process.getEnd());
    }
}
