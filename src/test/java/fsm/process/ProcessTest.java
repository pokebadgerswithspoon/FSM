package fsm.process;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static fsm.process.ProcessUtil.run;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class ProcessTest {

    private static final Guard<Map, Object> ALLOW = (r,p) -> true;
    @Mock
    Action<Map, Object> A;
    @Mock
    Action<Map, Object> B;
    Action logE = (r, p) -> log.info("Event");

    @Test
    public void emptyExample() {
        Process<Integer, String, Map>  process = ProcessBuilder.builder()
                .start()
                .end()
                .build();

        FsmDefinition<Integer, String,Map> def = process.getFsmDefinition();
        log(process);
        assertEquals(2, def.states().size());

        run(process);
    }

    @Test
    public void plainExample() {
        Process<Integer, String, Map> process = ProcessBuilder.builder()
            .start()
            .then(A)
            .end()
            .build();

        FsmDefinition<Integer, String, Map> def = process.getFsmDefinition();
        assertEquals(3, def.states().size());

        log(def);
        run(process);
        verify(A, times(1)).execute(any(), any());
    }

    @Test
    public void loopExample() {
        Ref<Integer> refA = new Ref<>();
        ProcessBuilder.builder()
                .start()
                .then(A, refA)
                .go(refA);
    }

    @Test
    public void eventExample() {
        Ref<Integer> refB = new Ref<>("Ref B");
        Process<Integer, String, Map> process = ProcessBuilder.builder()
                .start()
                .waitFor(events -> events.add("EVENT", refB))
                .add(refB, (b) -> b.then(B).end())
                .end()
                .build();

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(4, fsmDefinition.states().size());
        log(process);
        run(process);
        verify(B, times(1)).execute(any(), any());
    }

    @Test
    public void eventsExample() {
        Process<Integer, String, Map>  process = ProcessBuilder.builder()
                .start()
                .then(A)
                .waitFor(
                        events -> events
                                .add("TIMEOUT", ProcessBuilder.EndSyntax::end)
                                .add("HELLO", b -> b.then(B).end())
                )
                .add(new Ref<>(), ProcessBuilder.EndSyntax::end)
                .end()
                .build();

        FsmDefinition fsmDefinition = process.getFsmDefinition();

        log(process);
        assertEquals(7, fsmDefinition.states().size());

        run(process);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(0)).execute(any(), any());
    }

    @Test
    public void chooseExample() {

        Ref<Integer> refE = new Ref<>();
        Process<Integer, String, Map> process = ProcessBuilder.builder()
                .start()
                .choose(
                        choose -> choose
                                .when(ALLOW, b -> b.then(B).go(refE))
                                .otherwise(refE)
                )
                .add(refE, ProcessBuilder.EndSyntax::end)
                .end()
                .build();

        log(process);

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(5, fsmDefinition.states().size());

        run(process);
        verify(B, times(1)).execute(any(), any());
    }


    private void log(FsmDefinition<Integer, String, Map> def) {
        log.info("State is:{}", def.define(null, 0));
    }

    private void log(Process<Integer, String,Map> process) {
        log(process.getFsmDefinition());
        log.info("start: {}, end: {}", process.getStart(), process.getEnd());
    }
}
