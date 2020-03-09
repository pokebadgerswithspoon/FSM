package fsm.process;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import fsm.process.Process.Ref;

import static fsm.process.ChooseSyntax.choose;
import static fsm.process.Process.start;
import static fsm.process.StayExitClause.exit;
import static org.junit.Assert.assertEquals;

@Slf4j
public class BuilderTest {
    Action A = (r, p) -> log.info("A");
    Action B = (r, p) -> log.info("B");
    Action logE = (r, p) -> log.info("Event");


//
//
//    private void run(FsmDefinition def) {
//        Fsm fsm = def.define(null, 0);
//        while (def.hasHandler(fsm.getState(), Process.ENTER)) {
//            fsm.handle(Process.ENTER);
//        }
//    }


    @Test
    public void emptyExample() {
        FsmDefinition def = start()
                .end();
        assertEquals(2, def.states().size());
//        run(def);

    }

    @Test
    public void plainExample() {
        FsmDefinition def = start()
                .then(A)
                .then(B)
                .end();
        assertEquals(4, def.states().size());
//        run(def);
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
                .end();
        assertEquals(4, fsmDefinition.states().size());
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
        assertEquals(4, fsmDefinition.states().size());
    }


    @Test
    public void chooseExample() {
        Action E = (r, p) -> log.info("E");
        Ref refE = new Ref();
        FsmDefinition fsmDefinition = start()
                .then(A)
                .choose(
                        choose()
                                .when(Guard.ALLOW, (b) -> b.then(B).go(refE))
                                .otherwise((b) -> b.go(refE))
                )
                .add(refE)
                .end();

        assertEquals(5, fsmDefinition.states().size());

    }
}
