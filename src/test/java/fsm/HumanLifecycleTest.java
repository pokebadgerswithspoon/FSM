/*
 * HumanLifecycleTest.java
 * Created on 28 Feb 2019 17:21:16
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import static fsm.Action.TAKE_NO_ACTION;
import static fsm.HumanLifecycleTest.State.*;
import static fsm.HumanLifecycleTest.Events.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author lauri
 */
public class HumanLifecycleTest {

    enum State {
        INIT,
        AWAKE,
        ASLEEP
    }

    enum Events {
        BIRTH,
        TICK,
        WAKE,
    }

    class HumanBody {
        int age = 0;
        int food = 0;
        int tireness = 0;
        
    }

    @Test
    public void testBabyFsm() {

        FsmDefinition<State, Events, HumanBody> baby = new FsmDefinition();

        baby.in(INIT).on(BIRTH).transition((body, context) -> {body.age++; log(body);}).to(AWAKE);
        baby.in(AWAKE).on(TICK).transition(TAKE_NO_ACTION).to(ASLEEP);
        baby.in(ASLEEP).on(WAKE).transition(TAKE_NO_ACTION).to(AWAKE);
        

        Fsm fsm = baby.define(new HumanBody(), INIT);

        fsm.handle(new Event.Impl(BIRTH));
        assertEquals(AWAKE, fsm.getState());

        fsm.handle(new Event.Impl(BIRTH));


    }
    
    private static void log(Object o) {
        System.out.println(o);
    }
}
