package fsm.process.impl;

import fsm.process.ProcessBuilder;
import fsm.process.StateFactory;

public class ProcessBuilderInitImpl<S,E,R> implements ProcessBuilder.StartSyntax<S,E,R> {
    private StateFactory<S> stateFactory;

    public ProcessBuilder.StartSyntax<S,E,R> setRefFactory(StateFactory<S> stateFactory) {
        this.stateFactory = stateFactory;
        return this;
    }

    @Override
    public ProcessBuilder.StartedSyntax<S,E,R> start() {
        return new ProcessBuilderImpl.Started<S,E,R>(stateFactory);
    }
}
