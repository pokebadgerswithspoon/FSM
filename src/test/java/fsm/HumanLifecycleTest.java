/*
 * HumanLifecycleTest.java
 * Created on 28 Feb 2019 17:21:16
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import org.junit.Test;

import static fsm.HumanLifecycleTest.Events.*;
import static fsm.HumanLifecycleTest.State.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author lauri
 */
public class HumanLifecycleTest {

    enum State {
        INIT,
        AWAKE,
        ASLEEP,
        DEAD
    }

    enum Events {
        BIRTH,
        TICK,
        EAT,
        WAKE,
    }

    class HumanBody {

        int ageTicks = 0;
        int food = 10;
        int tireness = 0;
    }

    @Test
    public void testBabyFsm() {

        FsmDefinition<State, Events, HumanBody> human = new FsmDefinition<>();

        human.on(TICK).transition((body, p) -> {
            body.ageTicks++;
            body.food--;
            body.tireness++;
            log("body.food  = " +body.food);
        }).keepState();
        human.on(TICK).onlyIf((body, payload) ->  body.food < 0).transition().to(DEAD);

        human.in(INIT).on(BIRTH).transition().to(AWAKE);
        human.in(AWAKE).on(TICK).transition().to(ASLEEP);
        human.in(AWAKE).on(EAT).transition((body, p) -> {
            body.food++;
        }).keepState();
        human.in(ASLEEP).on(WAKE).transition().to(AWAKE);

        human.in(AWAKE).on(TICK).transition().to(ASLEEP);


        Fsm<State, Events, HumanBody> masha = human.define(new HumanBody(), INIT);

        masha.handle(BIRTH);
        assertEquals(AWAKE, masha.getState());
        masha.handle(TICK);

        masha.handle(BIRTH);

        assertTrue(masha.getRuntime().ageTicks > 0);

        for (int i = 0; i < 15; i++) {
            masha.handle(TICK);
        }
        assertEquals(DEAD, masha.getState());
    }

    private static void log(Object o) {
//        System.out.println(o);
    }
}
