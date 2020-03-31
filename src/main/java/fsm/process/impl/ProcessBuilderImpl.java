package fsm.process.impl;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.Process;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import fsm.process.StateFactory;
import fsm.process.StayUntil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static fsm.Action.TAKE_NO_ACTION;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class ProcessBuilderImpl<S> implements ProcessBuilder<S>, ProcessBuilder.ProceedSyntax<S>, ProcessBuilder.StartedSyntax<S> {

    private final StateFactory<S> stateFactory;
    final Ref<S> endRef;
    final Ref<S> startRef;

    Map<Ref<S>, Node<S>> nodes = new HashMap<>();
    private Node<S> current;


    public ProcessBuilderImpl() {
        this((StateFactory<S>) new IntStateFactory());
    }
    public ProcessBuilderImpl(StateFactory<S> stateFactory) {
        this(stateFactory, new Ref<>("START"), new Ref<>("END"));
    }

    private ProcessBuilderImpl(StateFactory<S> stateFactory, Ref<S> startRef, Ref<S> endRef) {
        requireNonNull(startRef);
        requireNonNull(endRef);
        requireNonNull(stateFactory);
        this.stateFactory = stateFactory;
        this.endRef = endRef;
        this.startRef = startRef;
        this.current = new Node<>( startRef, TAKE_NO_ACTION);
        this.current.processBuilder = this;
    }

    private void requireRegistered(Ref ref) {
        if (!ref.isAssigned()) {
            throw new IllegalStateException(format("Ref %s must be known to process, consider .register()", ref));
        }
    }


    @Override
    public ProcessBuilder.StartedSyntax then(Action action) {
        current = current.then(action);
        return this;
    }

    @Override
    public StartedSyntax then(Action action, Ref ref) {
        current = current.then(action, ref);
        return this;
    }


    @Override
    public ProceedSyntax<S> choose(ChooseSyntax chooseSyntax) {
        current.choose(chooseSyntax);
        return this;
    }

    public ProcessBuilderImpl<S> createSubProcessBuilder(Ref startRef) {
        return new ProcessBuilderImpl<>(this.stateFactory, startRef, endRef);
    }

    @Override
    public ProcessBuilder.ProceedSyntax stay(StayUntil exitClause) {
        current.stay(exitClause);
        return this;
    }


    @Override
    public ProcessBuilder.ProceedSyntax<S> add(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax> process) {
        ProcessBuilderImpl<S> subProcessBuilder = createSubProcessBuilder(ref);
        process.accept(subProcessBuilder);
        return this;
    }

    @Override
    public void go(Ref ref) {
        throw new IllegalArgumentException("Not implemented yet");
    }



    @Override
    public ProcessBuilder end() {
        then(TAKE_NO_ACTION, endRef);
        return this;
    }

    @Override
    public Process build() {
        final FsmDefinition definition = new FsmDefinition();
        nodes.keySet().forEach(ref -> register(ref, definition));
        return new ProcessImpl(
            startRef.getState(),
            endRef.getState(),
            definition
        );
    }



    private Ref register(final Ref<S> ref, FsmDefinition fsmDefinition) {
        requireNonNull(ref);
        if (!ref.isAssigned()) {
            S state = stateFactory.createState(ref);
            ref.setState(state);
            fsmDefinition.registerState(ref.getState());
        }
        return ref;
    }

}
