package fsm.process;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;

import static fsm.process.ChooseSyntax.choose;
import static fsm.process.ProcessUtil.run;
import static fsm.process.StayUntil.stayUntil;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class ProcessBuilderTest {
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
        Consumer<ProcessBuilder.StartedSyntax> dfdsf = (s) -> s.end();
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
            .end();

        FsmDefinition fsmDefinition = process.getFsmDefinition();

        log(process);
        assertEquals(6, fsmDefinition.states().size());

        run(fsmDefinition);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(0)).execute(any(), any());
    }


    @Test
    public void chooseExample() {
        Ref refE = new Ref();
        Consumer<ProcessBuilder.StartedSyntax> sdf = b -> b.end();
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

    @Test
    public void testBranching() {
        Ref refE = new Ref();

        Ref refD = new Ref();
        Consumer<ProcessBuilder.StartedSyntax> sdf = b -> b.go(refD);
        Process process = ProcessBuilder.builder()
            .start()
            .then(A)
            .choose(
                choose()
                    .when(Guard.ALLOW, (b) -> b.then(B).go(refE))
                    .otherwise((b) -> b.go(refE))
            )
            .add(refE, sdf)
            .add(refD)
            .end();

        log(process);

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(5, fsmDefinition.states().size());

        run(process);
        verify(B, times(1)).execute(any(), any());
    }





    private void log(Process process) {
        log(process.getFsmDefinition());
        log.info("add: {}, end: {}", process.getStart(), process.getEnd());
    }
}
