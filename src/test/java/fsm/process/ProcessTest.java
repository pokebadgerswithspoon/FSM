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
import static org.mockito.Mockito.mock;
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

    /**
     * <img src="doc-files/emptyExample.png"/>
     * <a href="doc-files/emptyExample.bpmn20.xml">BPMN</a>
     */
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

    /**
     * <img src="doc-files/plainExample.png"/>
     * <a href="doc-files/plainExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void plainExample() {
        Process<Integer, String, Map> process = ProcessBuilder.builder()
            .start()
            .then(new Ref<>("A"), A)
            .end()
            .build();

        FsmDefinition<Integer, String, Map> def = process.getFsmDefinition();
        assertEquals(3, def.states().size());

        log(process);
        run(process);
        verify(A, times(1)).execute(any(), any());
    }

    /**
     * <img src="doc-files/loopExample.png"/>
     * <a href="doc-files/loopExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void loopExample() {
        Ref<Integer> refA = new Ref<>();

        ProcessBuilder.builder()
                .start()
                .then(refA, A)
                .choose(choose -> choose.when(ALLOW, p -> p.jump(refA))
                                    .otherwise(p -> p.end())
                )
                .end();
    }

    /**
     * <img src="doc-files/eventExample.png"/>
     * <a href="doc-files/eventExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void eventExample() {
        Ref<Integer> refB = new Ref<>("Ref B");
        Process<Integer, String, Map> process = ProcessBuilder.builder()
                .start()
                .stay(events -> events.on("EVENT", refB))
                .then(refB, B)
                .end()
                .build();

        FsmDefinition fsmDefinition = process.getFsmDefinition();
//        assertEquals(4, fsmDefinition.states().size());
        log(process);
        run(process);
        verify(B, times(1)).execute(any(), any());
    }

    /**
     * <img src="doc-files/eventsExample.png"/>
     * <a href="doc-files/eventsExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void eventsExample() {
        Ref<Integer> cRef = new Ref<>();
        Action<Map,Object> C = mock(Action.class);

        Process<Integer, String, Map>  process = ProcessBuilder.builder()
                .start()
                .thenStay(A,
                        leave -> leave
                                .on("TIMEOUT",
                                        p -> p.jump(cRef)
                                )
                                .on("HELLO", b -> b.then(B).end())
                )
                .then(cRef, C)
                .end()
                .build();

        log(process);
        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(7, fsmDefinition.states().size());

        run(process);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(0)).execute(any(), any());
        verify(C, times(1)).execute(any(), any());
    }

    @Test
    public void eventsExample2() {
        Action<Map, Object> HEY = (r,p) -> {};
        Process<Integer, String, Map>  process = ProcessBuilder.builder()
                .start()
                .thenStay(A,
                        leave -> leave
                                .on("TIMEOUT", b -> b.end())
                                .on("HELLO", b -> b.then(B).end())
                )
                .then(HEY)
                .end()
                .build();


        log(process);

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(7, fsmDefinition.states().size());

        run(process);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(0)).execute(any(), any());
    }


    /**
     * <img src="doc-files/chooseExample.png"/>
     * <a href="doc-files/chooseExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void chooseExample() {

        Ref<Integer> refE = new Ref<>();
        Process<Integer, String, Map> process = ProcessBuilder.builder()
                .start()
                .choose(
                        choose -> choose
                                .when(ALLOW, b -> b.then(B).end())
                                .otherwise(e -> e.end())
                )
                .label(refE)
                .end()
                .build();

        log(process);

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(6, fsmDefinition.states().size());

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
