package fsm.process.impl;

import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class Branch<S,E,R> implements ProcessBuilder.ProceedSyntax<S,E,R> {
    final Ref<S> startRef;
    final Ref<S> endRef;
    final ProcessBuilderImpl<S,E,R> processBuilder;

    @Override
    public ProcessBuilder.ProceedSyntax<S,E,R> sub(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> process) {
        ProcessBuilderImpl<S,E,R> subProcessBuilder = processBuilder.createSubProcessBuilder(ref);
        process.accept(subProcessBuilder);
        return processBuilder;
    }

    @Override
    public ProcessBuilder<S,E,R> end() {
        return processBuilder.end();
    }
}
