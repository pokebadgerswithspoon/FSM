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

    static final Action<Runtime, Integer> increment = (runtime, e) -> {
        runtime.knock++;
    };
    static final Action<Runtime, Integer> log = (runtime, e) -> {
        System.out.println("Knock "+ runtime.knock);
        System.out.println("Payload: "+ e);
    };

    @Test
    public void testDoubleMatchWithKeepState() {

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition<>();

        Action<Runtime, Integer> action = Action.combine(increment, log);

        rules.on("KNOCK", Integer.class).transition(action).keepState();
        rules.in("INIT").on("KNOCK", Integer.class).transition(action).keepState();

        Runtime runtime = new Runtime();
        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle("KNOCK", 1234);

        assertEquals(2, runtime.knock);
    }

    @Test
    public void testDoubleMatch() {

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition();
        rules.on("KNOCK", Integer.class).transition(increment).keepState();
        rules.in("INIT").on("KNOCK", Integer.class).transition(increment).to("THE END");

        Fsm<String, String, Runtime> fsm = rules.define(new Runtime(), "INIT");

        fsm.handle("KNOCK");
        fsm.handle("KNOCK");

        assertEquals(3, fsm.getRuntime().knock);
        assertEquals("THE END", fsm.getState());
    }
    
    @Test
    public void testDoubleMatch2() {

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition<>();
        rules.in("INIT").on("KNOCK", Integer.class).transition(increment).keepState();
        rules.in("INIT").on("KNOCK", Integer.class).transition(increment).to("THE END");

        Runtime runtime = new Runtime();
        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle("KNOCK");
        fsm.handle("KNOCK");

        assertEquals(1, runtime.knock);
        assertEquals("THE END", fsm.getState());
    }    

    static class Runtime {

        private int knock = 0;

    }

}
