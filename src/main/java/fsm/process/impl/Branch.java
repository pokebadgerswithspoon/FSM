package fsm.process.impl;

import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class Branch<S> implements ProcessBuilder.ProceedSyntax<S> {
    final Ref<S> startRef;
    final Ref<S> endRef;
    final ProcessBuilderImpl<S> processBuilder;

    @Override
    public ProcessBuilder.ProceedSyntax<S> add(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax> process) {
        ProcessBuilderImpl<S> subProcessBuilder = processBuilder.createSubProcessBuilder(ref);
        process.accept(subProcessBuilder);
        return processBuilder;
    }

    @Override
    public ProcessBuilder<S> end() {
        return processBuilder.end();
    }
}
