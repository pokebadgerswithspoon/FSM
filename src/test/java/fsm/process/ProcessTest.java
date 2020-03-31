package fsm.process;

import fsm.Action;
import fsm.FsmDefinition;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;

import static fsm.process.ProcessUtil.run;
import static fsm.process.StayUntil.stayUntil;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class ProcessTest {

    @Mock
    Action A;
    @Mock
    Action B;
    Action logE = (r, p) -> log.info("Event");

    @Test
    public void emptyExample() {
        Process process = ProcessBuilder.builder()
                .start()
                .end()
                .build();

        FsmDefinition def = process.getFsmDefinition();
        log(process);
        assertEquals(2, def.states().size());

        run(process);
    }

    @Test
    public void plainExample() {
        Process process = ProcessBuilder.builder()
                .start()
                .then(A)
                .end()
                .build();

        FsmDefinition def = process.getFsmDefinition();
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
        Consumer<ProcessBuilder.StartedSyntax> sdf = (b) -> b.end();
        Process process = ProcessBuilder.builder()
                .start()
                .stay(
                        stayUntil()
                                .on("EVENT", refB)
                )
                .add(refB, sdf)
                .end()
                .build();

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(3, fsmDefinition.states().size());

        run(process);
    }


    private void log(FsmDefinition def) {
        log.info("State is:{}", def.define(null, 0));
    }

    private void log(Process process) {
        log(process.getFsmDefinition());
        log.info("start: {}, end: {}", process.getStart(), process.getEnd());
    }
}
