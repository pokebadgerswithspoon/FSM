package fsm.process;

import fsm.Action;
import fsm.Fsm;
import fsm.FsmDefinition;
import fsm.Guard;
import fsm.process.Process.Ref;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static fsm.process.ChooseSyntax.choose;
import static fsm.process.Process.start;
import static fsm.process.StayExitClause.exit;
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

    private void run(FsmDefinition def) {
        Fsm fsm = def.define(null, 0);
        log.info("State is {}", fsm.getState());

        Function<Object, Optional<String>> whatEvent = (state) -> Stream.of("then", "timeout", "hello").filter((e) -> def.hasHandler(state, e)).findFirst();

        int i=0;
        for(Optional e = whatEvent.apply(fsm.getState()); e.isPresent(); ) {
            Object event = e.get();
            Object state = fsm.getState();
            fsm.handle(event);
            log.info("State {}, after [{}] is {}", state, event, fsm.getState());
            if(i ++ > 10) {
                throw new IllegalStateException("Too much steps");
            }
            e = whatEvent.apply(fsm.getState());
        }

    }


    @Test
    public void emptyExample() {
        FsmDefinition def = start()
                .end();
        assertEquals(2, def.states().size());

        run(def);
    }

    @Test
    public void plainExample() {
        FsmDefinition def = start()
                .then(A)
                .then(B)
                .end();
        assertEquals(4, def.states().size());

        run(def);
        verify(A, times(1)).execute(any(), any());
        verify(B, times(1)).execute(any(), any());
    }


    @Test
    public void loopExample() {
        Ref refA = new Ref();
        start()
                .then(A, refA)
                .then(B)
                .go(refA);
    }

    @Test
    public void eventExample() {
        Ref refB = new Ref("Ref B");
        FsmDefinition fsmDefinition = start()
                .then(A)
                .stay(
                        exit()
                        .on("event", refB)
                )
                .add(refB)
                .then(B)
                .end();
        assertEquals(5, fsmDefinition.states().size());

        run(fsmDefinition);
        verify(A, times(1)).execute(any(), any());
        verify(B, times(1)).execute(any(), any());
    }

    @Test
    public void eventsExample() {
        FsmDefinition fsmDefinition = start()
                .then(A)
                .stay(
                        exit()
                                .on("timeout", (b) -> b.end())
                                .on("hello", (b) -> b.then(B).end())
                )
                .end();
        assertEquals(6, fsmDefinition.states().size());

        run(fsmDefinition);

        verify(A, times(1)).execute(any(), any());
        verify(B, times(0)).execute(any(), any());
    }


    @Test
    public void chooseExample() {
        Action E = (r, p) -> log.info("E");
        Ref refE = new Ref("Stage E");
        FsmDefinition fsmDefinition = start()
                .then(A)
                .choose(
                        choose()
                                .when(Guard.ALLOW, (b) -> b.then(B).go(refE))
                                .otherwise((b) -> b.go(refE))
                )
                .add(refE)
                .then(E)
                .end();

        assertEquals(6, fsmDefinition.states().size());

    }
}
