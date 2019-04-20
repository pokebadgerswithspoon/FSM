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

    static Action<Runtime> increment = (Runtime runtime) -> {
        runtime.knock++;
    };
    static Action<Runtime> log = (Runtime runtime) -> {
        System.out.println("Knock "+ runtime.knock);
    };

    @Test
    public void testDoubleMatchWithKeepState() {

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition();
        Action<Runtime> increment = (Runtime runtime) -> {
            runtime.knock++;
        };
        Action<Runtime> action = Action.combine(increment, log);
        
        rules.on("KNOCK").transition(action).keepState();
        rules.in("INIT").on("KNOCK").transition(action).keepState();

        Runtime runtime = new Runtime();
        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle(new Event.Impl("KNOCK"));

        assertEquals(2, runtime.knock);
    }

    @Test
    public void testDoubleMatch() {

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition();
        rules.on("KNOCK").transition(increment).keepState();
        rules.in("INIT").on("KNOCK").transition(increment).to("THE END");

        Runtime runtime = new Runtime();
        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle(new Event.Impl("KNOCK"));
        fsm.handle(new Event.Impl("KNOCK"));

        assertEquals(3, runtime.knock);
        assertEquals("THE END", fsm.getState());
    }
    
    @Test
    public void testDoubleMatch2() {

        FsmDefinition<String, String, Runtime> rules = new FsmDefinition();
        rules.in("INIT").on("KNOCK").transition(increment).keepState();
        rules.in("INIT").on("KNOCK").transition(increment).to("THE END");

        Runtime runtime = new Runtime();
        Fsm<String, String, Runtime> fsm = rules.define(runtime, "INIT");

        fsm.handle(new Event.Impl("KNOCK"));
        fsm.handle(new Event.Impl("KNOCK"));

        assertEquals(1, runtime.knock);
        assertEquals("THE END", fsm.getState());
    }    

    static class Runtime {

        private int knock = 0;

    }

}
