package fsm.process.impl;

import fsm.Guard;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

import static fsm.Guard.ALLOW;

@RequiredArgsConstructor
class EventSyntaxImpl<S,E,R> implements ProcessBuilder.EventSyntax<S,E,R> {
    private final Node<S,E,R> node;

    @Override
    public ProcessBuilder.EventSyntax<S, E, R> on(E event, Guard<R, Object> guard, Ref<S> refTo) {
        node.addExit(event, refTo, guard == null ? ALLOW : guard);
        return this;
    }

    @Override
    public ProcessBuilder.EventSyntax<S, E, R> on(E event, Guard<R, Object> guard, ProcessBuilder.SubProcess process) {
        Ref<S> ref = new Ref<>();
        ProcessBuilderImpl<S, E, R> subProcessBuilder = node.processBuilder.createSubProcessBuilder(ref);
        process.apply(subProcessBuilder);
        Exit<S, E, R> exit = new Exit<>(ref, event, guard);
        node.addExit(exit);
        return this;
    }

    //    @Override
//    public ProcessBuilder.EventSyntax<S, E, R> on(E event, Guard<R, Object> guard, ProcessBuilder.SubProcess process) {
//        Ref<S> ref = new Ref<>();
//        ProcessBuilderImpl<S, E, R> subProcessBuilder = node.processBuilder.createSubProcessBuilder(ref);
//        process.accept(subProcessBuilder);
//        Exit<S, E, R> exit = new Exit<>(ref, event, guard);
//        node.addExit(exit);
//        return this;
//    }

//    @Override
//    public ProcessBuilder.EventSyntax<S, E, R> on(E event, Guard<R, Object> guard, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> process) {
//        Ref<S> ref = new Ref<>();
//        ProcessBuilderImpl<S, E, R> subProcessBuilder = node.processBuilder.createSubProcessBuilder(ref);
//        process.accept(subProcessBuilder);
//        Exit<S, E, R> exit = new Exit<>(ref, event, guard);
//        node.addExit(exit);
//        return this;
//    }
}
