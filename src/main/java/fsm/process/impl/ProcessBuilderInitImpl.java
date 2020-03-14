package fsm.process.impl;

import fsm.process.ProcessBuilder;

public class ProcessBuilderInitImpl<S> implements ProcessBuilder.InitSyntax<S>, ProcessBuilder.StartSyntax<S> {
    private ProcessBuilder.RefFactory<S> refFactory;

    @Override
    public ProcessBuilder.StartSyntax<S> setRefFactory(ProcessBuilder.RefFactory<S> refFactory) {
        this.refFactory = refFactory;
        return this;
    }

    @Override
    public ProcessBuilder.StartedSyntax<S> start() {
        return new ProcessBuilderImpl<>(refFactory);
    }
}
