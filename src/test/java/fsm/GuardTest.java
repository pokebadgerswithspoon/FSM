package fsm;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static fsm.Guard.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuardTest {
    static Function<Integer, Guard<Integer, Integer>> greaterThanX = x -> (r, p) -> p > x;
    static Function<Integer, Guard<Integer, Integer>> lessThanX = x -> (r, p) -> p < x;

    static Guard<Integer, Integer> greaterThan5 = greaterThanX.apply(5);
    static Guard<Integer, Integer> lessThan10 = lessThanX.apply(10);

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
        Guard<Integer, Integer> gt5AndLt10 = and(greaterThan5, lessThan10);
        assertTrue(gt5AndLt10.allow(null, 7));
        assertFalse(gt5AndLt10.allow(null, 10));
        assertTrue(and(Arrays.asList(greaterThan5, lessThan10)).allow(null, 7));
    }

    @Test
    public void testOr() {
        Guard<Integer, Integer> gt5OrLt10= or(greaterThan5, lessThan10);
        assertTrue(gt5OrLt10.allow(null, 7));
        assertTrue(gt5OrLt10.allow(null, 10));
    }

    @Test
    public void testUnless() {
        Guard<Integer, Integer> greaterThan0 = greaterThanX.apply(0);
        Guard<Integer, Integer> lessThan3 = lessThanX.apply(3);

        Guard<Integer, Integer> a = and(greaterThan0, lessThan3);
        Guard<Integer, Integer> b = and(greaterThan5, lessThan10);

        Guard<Integer, Integer> c = unless(a, b);
        assertTrue(c.allow(null, 20));
        assertTrue(c.allow(null, 4));
        assertFalse(c.allow(null, 2));
        assertFalse(c.allow(null, 7));
    }
}