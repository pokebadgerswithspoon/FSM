package fsm.process.impl;

import fsm.process.ProcessBuilder;
import fsm.process.StateFactory;

public class ProcessBuilderInitImpl<S> implements ProcessBuilder.StartSyntax<S> {
    private StateFactory<S> stateFactory;

    public ProcessBuilder.StartSyntax<S> setRefFactory(StateFactory<S> stateFactory) {
        this.stateFactory = stateFactory;
        return this;
    }

    @Override
    public ProcessBuilder.StartedSyntax<S> start() {
        return new ProcessBuilderImpl<>(stateFactory);
    }
}
