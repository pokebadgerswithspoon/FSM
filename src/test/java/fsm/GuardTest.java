package fsm;

import org.junit.Test;

import java.util.Arrays;

import static fsm.Guard.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuardTest {

    @Test
    public void testAllow() {
        assertTrue(ALLOW.allow(null, null));
    }

    @Test
    public void testNot() {
        assertFalse(not(ALLOW).allow(null, null));
    }

    @Test
    public void testAnd() {
        Guard<Integer, Integer> greaterThan5 = (r, p) -> p > 5;
        Guard<Integer, Integer> lessThan10 = (r, p) -> p < 10;
        Guard<Integer, Integer> gt5AndLt10 = and(greaterThan5, lessThan10);
        assertTrue(gt5AndLt10.allow(null, 7));
        assertFalse(gt5AndLt10.allow(null, 10));
        assertTrue(and(Arrays.asList(greaterThan5, lessThan10)).allow(null, 7));
    }


    @Test
    public void testOr() {
        Guard<Integer, Integer> greaterThan5 = (r, p) -> p > 5;
        Guard<Integer, Integer> lessThan10 = (r, p) -> p < 10;
        Guard<Integer, Integer> gt5OrLt10= or(greaterThan5, lessThan10);
        assertTrue(gt5OrLt10.allow(null, 7));
        assertTrue(gt5OrLt10.allow(null, 10));
    }
}