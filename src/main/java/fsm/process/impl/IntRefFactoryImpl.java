package fsm.process.impl;

import fsm.process.Process.Ref;
import fsm.process.ProcessBuilder;

public class IntRefFactoryImpl implements ProcessBuilder.RefFactory<Integer> {
    private int lastKnownState = -1;

    @Override
    public Ref<Integer> create() {
        return create(null);
    }

    @Override
    public Ref<Integer> create(String name) {
        return new Ref<>(name);
    }

    @Override
    public boolean hasState(Ref<Integer> ref) {
        return ref.state != null;
    }

    @Override
    public void assignState(Ref<Integer> ref) {
        ref.state = ++lastKnownState;
    }
}
