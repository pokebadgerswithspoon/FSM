package fsm.process.impl;

import fsm.process.Ref;
import fsm.process.StateFactory;

import java.util.function.Supplier;

public class IntStateFactory implements StateFactory<Integer> {
    private static final Supplier<IllegalArgumentException> NOT_OUR_REF = () -> new IllegalArgumentException("Ref is not generated by us");
    private int lastKnownState = -1;


    @Override
    public Integer createState(Ref<Integer> ref) {
        return ++lastKnownState;
    }
}
