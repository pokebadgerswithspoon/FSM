package fsm.process;

import fsm.Action;
import fsm.FsmDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static fsm.process.ProcessUtil.run;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SubProcessTest {

    @Mock
    private Action doSomething;

    @Test
    public void testSimpleProcess() {
        Process simpleProcess = createSimpleProcess();
        assertNotNull(simpleProcess);
//        FsmDefinition def = simpleProcess.getFsmDefinition();
//        assertEquals(3, def.states().size());
//
//        run(simpleProcess);
    }

//    @Test
//    public void testSubProcess() {
//        Process simpleProcess = createSimpleProcess();
//        ProcessBuilder.builder()
//            .start()
//            .then(simpleProcess)
//            .then(doSomething)
//            .end();
//        FsmDefinition def = simpleProcess.getFsmDefinition();
//        assertEquals(3, def.states().size());
//
//        run(simpleProcess);
//
//        verify(doSomething, times(2)).execute(any(), any());
//    }

    Process createSimpleProcess() {
        return ProcessBuilder.builder()
            .start()
            .then(doSomething)
            .end()
            .build();
    }
}
