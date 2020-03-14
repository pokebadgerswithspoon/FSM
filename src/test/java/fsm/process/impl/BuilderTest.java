package fsm.process.impl;

import fsm.Action;
import fsm.Fsm;
import fsm.FsmDefinition;
import fsm.Guard;
import fsm.process.Process.Ref;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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
        FsmDefinition def = new Process()
                .start()
                .end();
        assertEquals(2, def.states().size());

        run(def);
        log(def);
    }

    private void log(FsmDefinition def) {
        log.info("State is:{}", def.define(null, 0));
    }

    @Test
    public void plainExample() {
        FsmDefinition def = new Process().start()
                .then(A)
                .end();
        assertEquals(3, def.states().size());

        log(def);
        run(def);
        verify(A, times(1)).execute(any(), any());
    }


    @Test
    public void loopExample() {
        Ref refA = new Ref();
        new Process()
                .start()
                .then(A, refA)
                .then(B)
                .go(refA);
    }

    @Test(expected = ProcessDidNotEndException.class)
    public void gapExample() {
        Ref refB = new Ref("Ref B");
        Process process = new Process();

        FsmDefinition fsmDefinition = process
                .start()
                .add(refB)
                .end();

        assertEquals(3, fsmDefinition.states().size());
        log(process);
        run(process);
    }


    @Test
    public void eventExample() {
        Ref refB = new Ref("Ref B");
        Process process = new Process();
        FsmDefinition fsmDefinition = process
                .start()
                .then(A)
                .stay(
                        exit()
                                .on("event", refB)
                )
                .add(refB)
                .end();
//        assertEquals(4, fsmDefinition.states().size());

        log(process);

//        run(fsmDefinition);
//        verify(A, times(1)).execute(any(), any());
//        verify(B, times(1)).execute(any(), any());
    }

    @Test
    public void eventsExample() {
        Process process = new Process();
        FsmDefinition fsmDefinition = process.start()
                .then(A)
                .stay(
                        (exit) -> exit
                                .on("timeout", (Consumer<Process>) (b) -> b.end())
                                .on("hello", (Consumer<Process>) (b) -> b.then(B).end())
                )
                .add(new Ref())
                .end();

        log(process);
        assertEquals(7, fsmDefinition.states().size());

        run(fsmDefinition);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(0)).execute(any(), any());
    }


    @Test
    public void chooseExample() {
        Ref refE = new RefImpl("Stage E");
        Process process = new Process();
        FsmDefinition fsmDefinition = process.start()
                .then(A)
                .choose(
                        (choose) -> choose
                                .when(Guard.ALLOW, (b) -> b.then(B).go(refE))
                                .otherwise((b) -> b.go(refE))
                )
                .add(refE)
                .end();

        log(process);

        assertEquals(5, fsmDefinition.states().size());

        run(fsmDefinition);
        verify(B, times(1)).execute(any(), any());
    }


    private void run(Process process) {
        FsmDefinition def = process.definition;
        Fsm fsm = def.define(null, process.startRef.state);
        runFsm(def, fsm);
        if (!Integer.valueOf(process.endRef.state).equals(fsm.getState())) {
            throw new ProcessDidNotEndException("Process did not end well");
        }
    }

    private void run(FsmDefinition def) {
        runFsm(def, def.define(null, 0));
    }

    private void runFsm(FsmDefinition def, final Fsm fsm) {
        log.info("FSM is {}", fsm);

        Function<Object, Optional<String>> whatEvent = (state) -> Stream.of("then", "timeout", "hello").filter((e) -> def.hasHandler(state, e)).findFirst();

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
        log(process.definition);
        log.info("add: {}, end: {}", process.startRef, process.endRef);
    }
}
