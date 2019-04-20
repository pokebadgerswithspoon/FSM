/*
 * FsmTest.java
 * Created on 18 Apr 2019 19:39:27
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author lauri
 */
public class FsmTest {

    static Action<?> increment(Runtime runtime) {
        return (p) -> {
            runtime.knock++;
        };
    }
    static Action<?> log(Runtime runtime) {
        return (p) -> {
            System.out.println("Knock " + runtime.knock);
        };
    }

    @Test
    public void testDoubleMatchWithKeepState() {

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition();
        Runtime runtime = new Runtime();

        Action<?> log = log(runtime);
        Action<?> increment = increment(runtime);
        Action<?> action = (p) -> {
            increment.execute(null);
            log.execute(null);
        };

        rules.on("KNOCK").transition(action).keepState();
        rules.in("INIT").on("KNOCK").transition(action).keepState();

        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle(new Event("KNOCK"));

        assertEquals(2, runtime.knock);
    }

    @Test
    public void testDoubleMatch() {
        Runtime runtime = new Runtime();
        Action<?> increment = increment(runtime);

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition();
        rules.on("KNOCK").transition(increment).keepState();
        rules.in("INIT").on("KNOCK").transition(increment).to("THE END");

        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle(new Event("KNOCK"));
        fsm.handle(new Event("KNOCK"));

        assertEquals(3, runtime.knock);
        assertEquals("THE END", fsm.getState());
    }

    @Test
    public void testDoubleMatch2() {
        Runtime runtime = new Runtime();
        Action<?> increment = increment(runtime);

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition();
        rules.in("INIT").on("KNOCK").transition(increment).keepState();
        rules.in("INIT").on("KNOCK").transition(increment).to("THE END");

        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle(new Event("KNOCK"));
        fsm.handle(new Event("KNOCK"));

        assertEquals(1, runtime.knock);
        assertEquals("THE END", fsm.getState());
    }

    static class Runtime {

        private int knock = 0;

    }

}
