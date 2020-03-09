package fsm;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import fsm.Builder.Ref;
import static fsm.Builder.start;
import static fsm.ChooseSyntax.choose;
import static fsm.StayExitClause.exit;
import static org.junit.Assert.assertEquals;

@Slf4j
public class BuilderTest {
    Action A = (r, p) -> log.info("A");
    Action B = (r, p) -> log.info("B");
    Action logE = (r, p) -> log.info("Event");

    @Test
    public void chooseExample() {
        Action E = (r, p) -> log.info("E");
        Ref refE = new Ref();
        start()
                .then(A)
                .choose(
                        choose()
                       .when(Guard.ALLOW, (b) -> b.then(B).go(refE))
                       .otherwise((b) -> b.go(refE))
                ).then(E, refE)
                .end();
    }


    @Test
    public void eventsExample() {
        start()
                .then(A)
                .stay(
                        exit()
                        .on("timeout", (b) -> b.end())
                        .on("hello", (b) -> b.then(B).end())
                )
                .end();
    }

    @Test
    public void eventExample() {
        FsmDefinition fsmDefinition = start()
                .then(A)
                .stay(
                        exit()
                                .on("timeout", (b) -> b.then(B).end())
                )
                .end();
//        assertEquals(2, fsmDefinition.states().size());
        run(fsmDefinition);
    }


    @Test
    public void plainExample() {
        FsmDefinition def = start()
                .then(A)
                .then(B)
                .end();
        run(def);

    }

    @Test
    public void emptyExample() {
        FsmDefinition def = start()
                .end();
        run(def);

    }

    private void run(FsmDefinition def) {
        Fsm fsm = def.define(null, 0);
        while (def.hasHandler(fsm.getState(), Builder.ENTER)) {
            fsm.handle(Builder.ENTER);
        }
    }


    @Test
    public void loopExample() {
        Ref refA = new Ref();
        start()
                .then(A, refA)
                .then(B)
                .go(refA);
    }
}
