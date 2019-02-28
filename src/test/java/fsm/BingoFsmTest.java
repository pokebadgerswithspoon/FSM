/*
 * BingoFsmTest.java
 * Created on 23 Feb 2019 19:52:56
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import static fsm.Action.TAKE_NO_ACTION;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author lauri
 */
public class BingoFsmTest {
    
    @Test
    public void testBingoFsm() {
        
        FsmDefinition bingo = new FsmDefinition();
        bingo.in("INIT").on("HELLO").transition(TAKE_NO_ACTION).to("SALES");
        bingo.in("SALES").on("HELLO").transition(TAKE_NO_ACTION).to("ROLL");
        bingo.in("ROLL").on("HELLO").transition(TAKE_NO_ACTION).to("ANNOUNCE");
        bingo.in("ANNOUNCE").on("HELLO").transition(TAKE_NO_ACTION).to("END");
        
        
        Fsm fsm = bingo.define(null, "INIT");
        
        fsm.handle(new Event.Impl("HELLO"));
        
        assertEquals("SALES", fsm.getState());
        
    }
}
