package fsm.process.impl;

import fsm.Guard;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
class EventExitSyntaxImpl<S,E,R> implements ProcessBuilder.EventExitSyntax<S,E,R> {
    private final Node<S,E,R> node;
    private final Guard<R, Object> ALLOW = (r, p) -> true;

    @Override
    public ProcessBuilder.EventExitSyntax<S, E, R> on(E event, Ref<S> refTo, Guard<R, Object> guard) {
        node.addExit(event, refTo, guard == null?ALLOW:guard);
        return this;
    }

    @Override
    public ProcessBuilder.EventExitSyntax<S, E, R> on(E event, Guard<R, Object> guard, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> process) {
        Ref<S> ref = new Ref<>();
        ProcessBuilderImpl<S, E, R> subProcessBuilder = node.processBuilder.createSubProcessBuilder(ref);
        process.accept(subProcessBuilder);
        Exit<S, E, R> exit = new Exit<>(ref, event, guard);
        node.addExit(exit);
        return this;
    }
}
