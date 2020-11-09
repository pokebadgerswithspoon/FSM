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
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class ProcessTest {

    private static final Guard<Map, Object> ALLOW = (r, p) -> true;
    @Mock
    Action<Map, Object> A;
    @Mock
    Action<Map, Object> B;

    /**
     * <img src="doc-files/emptyExample.png"/>
     * <a href="doc-files/emptyExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void emptyExample() {
        Process<Integer, String, Map> process = ProcessBuilder.builder()
            .start()
            .end()
            .build();

        FsmDefinition<Integer, String, Map> def = process.getFsmDefinition();
        log(process);
        assertEquals("FSM behind empty process has two states: START and FINISH", 2, def.states().size());

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

        Process<?, ?, ?> process = ProcessBuilder.builder()
            .start()
            .then(refA, A)
            .choose(choose -> choose.when(ALLOW, p -> p.jump(refA))
                .otherwise(p -> p.end())
            )
            .end()
            .build();

        try {
            run(process);
            fail("Loop example is endless and should be aborted by a runner");
        } catch (IllegalStateException ex) {
            // 👍 good!👍.
        }
    }

    /**
     * <img src="doc-files/eventExample.png"/>
     * <a href="doc-files/eventExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void eventExample() {
        Ref<Integer> refA = new Ref<>("Ref A");
        Ref<Integer> refB = new Ref<>("Ref B");
        Process<?, ?, ?> process = ProcessBuilder.builder()
            .start()
            .stay(events -> events.on("EVENT", refB))
            .then(refA, A)
            .then(refB, B)
            .end()
            .build();

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(4, fsmDefinition.states().size());

        log(process);
        run(process);

        verify(B, times(1)).execute(any(), any());
        verify(A, times(0)).execute(any(), any());
    }

    /**
     * <img src="doc-files/eventsExample.png"/>
     * <a href="doc-files/eventsExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void eventsExample() {
        Ref<Integer> cRef = new Ref<>();
        Action<Map, Object> C = mock(Action.class);
        Action<Map, Object> D = mock(Action.class);

        Process<Integer, String, Map> process = ProcessBuilder.builder()
            .start()
            .thenStay(A,
                leave -> leave
                    .on("SOMETHING ELSE", b -> b.then(D).end())
                    .on("TIMEOUT", Guard.allow(),
                        p -> p.then(B).jump(cRef)
                    )
                    .on("TIMEOUT", Guard.allow(),
                        p -> p.then(B).jump(cRef)
                    )
            )
            .then(cRef, C)
            .end()
            .build();

        run(process);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(1)).execute(any(), any());
        verify(C, times(1)).execute(any(), any());
        verify(D, times(0)).execute(any(), any());
    }

    /**
     * <img src="doc-files/eventsExample.png"/>
     * <a href="doc-files/eventsExample.bpmn20.xml">BPMN</a>
     */
    @Test
    public void labelExample() {

        Ref<Integer> refA = new Ref<>();
        Ref<Integer> refB = new Ref<>();
        Process<Integer, String, Map> process = ProcessBuilder.builder()
            .start()
            .thenStay(Action.noop(),
                leave -> leave
                    .on("TIMEOUT", Guard.allow(),
                        p -> p.jump(refB)
                    )
            )
            .label(refA)
            .then(A)
            .label(refB)
            .then(B)
            .end()
            .build();

        log(process);
        run(process);

        verify(A, times(0)).execute(any(), any());
        verify(B, times(1)).execute(any(), any());
    }

    /**
     * <img src="doc-files/eventsExample2.png"/>
     * <a href="doc-files/eventsExample2.bpmn20.xml">BPMN</a>
     */
    @Test
    public void eventsExample2() {
        Action<Map, Object> HEY = (r, p) -> {
        };
        Process<?, ?, ?> process = ProcessBuilder.builder()
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
        Process<?, ?, ?> process = ProcessBuilder.builder()
            .start()
            .choose(
                choose -> choose
                    .when(ALLOW, b -> b.then(B).end())
                    .otherwise(e -> e.end())
            )
            .label(refE)
            .end()
            .build();

        FsmDefinition fsmDefinition = process.getFsmDefinition();
        assertEquals(6, fsmDefinition.states().size());

        run(process);
        verify(B, times(1)).execute(any(), any());
    }

    private void log(Process<?, ?, ?> process) {
        log.info("start: {}, end: {}", process.getStart(), process.getEnd());
    }
}
