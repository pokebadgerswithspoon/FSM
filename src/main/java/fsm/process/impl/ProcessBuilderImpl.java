package fsm.process.impl;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import fsm.process.Process;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import fsm.process.StateFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static fsm.Action.TAKE_NO_ACTION;
import static java.util.Objects.requireNonNull;

public class ProcessBuilderImpl<S,E,R> implements ProcessBuilder<S,E,R>, ProcessBuilder.ProceedSyntax<S,E,R>, ProcessBuilder.StartedSyntax<S,E,R> {

    private final StateFactory<S> stateFactory;
    final Ref<S> endRef;
    final Ref<S> startRef;

    final Map<Ref<S>, Node<S,E,R>> nodes;
    private Node<S,E,R> current;

    public ProcessBuilderImpl(StateFactory<S> stateFactory) {
        this(stateFactory, new Ref<>("START"), new Ref<>("END"));
    }

    private ProcessBuilderImpl(StateFactory<S> stateFactory, Ref<S> startRef, Ref<S> endRef) {
        this(stateFactory, startRef, endRef, new HashMap<>());
    }
    private ProcessBuilderImpl(StateFactory<S> stateFactory, Ref<S> startRef, Ref<S> endRef, Map<Ref<S>, Node<S,E,R>> nodes) {
        requireNonNull(startRef);
        requireNonNull(endRef);
        requireNonNull(stateFactory);
        requireNonNull(nodes);
        this.nodes = nodes;
        this.stateFactory = stateFactory;
        this.endRef = endRef;
        this.startRef = startRef;
        this.current = getNodeByRef(startRef);
    }

    Node<S,E,R> createNode(Ref<S> ref, Action<R, Object> action) {
        if(this.nodes.containsKey(ref)) {
            throw new IllegalArgumentException("Ref is already known "+ ref);
        }
        Node<S,E,R> node = new Node<>(ref, action);
        node.processBuilder = this;
        this.nodes.put(ref, node);
        return node;
    }

    Node<S,E,R> getNodeByRef(Ref<S> ref) {
        return nodes.computeIfAbsent(ref, (r) -> new Node<S, E, R>(this, r, TAKE_NO_ACTION));
    }


    @Override
    public ProcessBuilder.StartedSyntax<S, E, R> then(Action<R, Object> action) {
        current = current.then(action);
        return this;
    }

    @Override
    public StartedSyntax<S,E,R> then(Action<R, Object> action, Ref<S> ref) {
        current = current.then(action, ref);
        return this;
    }

    @Override
    public ProceedSyntax<S, E, R> choose(Function<ChooseSyntax<S, E, R>, ChooseSyntax.End<S,E,R>> choose) {
        current.choose(choose);
        current = null;
        return this;
    }

    public ProcessBuilderImpl<S,E,R> createSubProcessBuilder(Ref startRef) {
        return new ProcessBuilderImpl<S,E,R>(this.stateFactory, startRef, endRef, nodes);
    }

    @Override
    public ProceedSyntax<S, E, R> stay(Consumer<EventExitSyntax<S, E, R>> leave) {
        leave.accept(new EventExitSyntaxImpl<>(current));
        current = null;
        return this;
    }


    @Override
    public ProcessBuilder.ProceedSyntax<S,E,R> add(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> process) {
        ProcessBuilderImpl<S,E,R> subProcessBuilder = createSubProcessBuilder(ref);
        process.accept(subProcessBuilder);
        this.current = null;
        return this;
    }

    @Override
    public void go(Ref<S> ref) {
        current.go(ref);
    }



    @Override
    public ProcessBuilder<S,E,R> end() {
        getNodeByRef(endRef);
        if(this.current != null) {
            this.current.addExit(new Exit<S,E,R>(endRef, (E) Node.THEN, Guard.ALLOW));
        }
        return this;
    }

    @Override
    public Process<S,E,R> build() {
        final FsmDefinition definition = new FsmDefinition();
        nodes.keySet().forEach(ref -> register(ref, definition));
        nodes.entrySet()
                .forEach(e -> {
                    Ref<S> ref = e.getKey();
                    Node<S,E,R> node = e.getValue();
                    node.exits
                            .forEach(exit -> {
                                Node<S,E,R> nodeTo = nodes.get(exit.refTo);
                                definition.in(ref.getState()).on(exit.event).onlyIf(exit.guard).transition(nodeTo.onEnter).to(exit.refTo.getState());
                            });
                });
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
