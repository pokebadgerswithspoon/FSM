package fsm.process.impl;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.FsmDefinition.ExecutionOrder;
import fsm.Guard;
import fsm.process.Process;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import fsm.process.StateFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

abstract class ProcessBuilderImpl<S, E, R, SELF extends ProcessBuilder.InProcessSyntax<S,E,R,SELF>> implements ProcessBuilder<S, E, R>,
        ProcessBuilder.InProcessSyntax<S, E, R, SELF>,
        //        ProcessBuilder.EndSyntax<ProcessBuilder.BuilderSyntax<S,E,R>>,
        //        ProcessBuilder.StartedSyntax<S,E,R>,
        //        ProcessBuilder.SubProcessSyntax<S,E,R>,
        ProcessBuilder.FinishedSyntax {

    final StateFactory<S> stateFactory;
    final Ref<S> endRef;
    final Ref<S> startRef;

    final Map<Ref<S>, Node<S, E, R,?>> nodes;
    Node<S, E, R, ?> current;

    protected ProcessBuilderImpl(StateFactory<S> stateFactory, Ref<S> startRef, Ref<S> endRef, Map<Ref<S>, Node<S, E, R, ?>> nodes) {
        requireNonNull(startRef);
        requireNonNull(endRef);
        requireNonNull(stateFactory);
        requireNonNull(nodes);
        this.nodes = nodes;
        this.stateFactory = stateFactory;
        this.endRef = endRef;
        this.startRef = startRef;
    }

    Node<S, E, R, ?> createAndRegisterNode(Ref<S> ref, Action<R, Object> action) {
        if (this.nodes.containsKey(ref)) {
            Node<S, E, R, ?> node = this.nodes.get(ref);
            ActionBox.tryTake(node.onEnter, action, () -> new IllegalArgumentException("Ref is already known " + ref));
        }
        return nodes.computeIfAbsent(ref, (r) -> doCreateNode(r, action));
    }

    protected abstract Node<S,E,R,?> doCreateNode(Ref<S> ref, Action<R, Object> action);



    Node<S, E, R, ?> getNodeByRef(Ref<S> ref) {
        return nodes.computeIfAbsent(ref, (r) -> doCreateNode(r, new ActionBox<>()));
    }

    @Override
    public SELF label(Ref<S> ref) {
        requireNonNull(ref, "Can not label with null ref");
        if(current == null) { // ie block after choose or stay
            current = getNodeByRef(ref);
        } else {
            current = current.label(ref);
        }
        return (SELF) this;
    }

    @Override
    public SELF then(Action<R, Object> action) {
//        current = current.then(action);
//        return (SELF) this;
        return then(new Ref<>(), action);
    }

    @Override
    public SELF then(Ref<S> ref, Action<R, Object> action) {
        if(current == null) { // ie block after choose or stay
            current = createAndRegisterNode(ref, action);
        } else {
            current = current.then(ref, action);
        }

        return (SELF) this;
    }


    @Override
    public SELF thenStay(Action<R, Object> action, Consumer<EventSyntax<S, E, R>> leave) {
        this.then(action);
        leave.accept(new EventSyntaxImpl<>(current));
        current = null;
        return (SELF) this;
    }

    @Override
    public SELF choose(Function<ChooseSyntax<S, E, R>, ChooseSyntax.End> choose) {
        current.choose(choose);
        current = null;
        return (SELF) this;
    }

    ProcessBuilderImpl.Sub<S,E,R> createSubProcessBuilder(Ref<S> startRef) {
        return new ProcessBuilderImpl.Sub(this.stateFactory, startRef, endRef, nodes);
    }

    @Override
    public SELF stay(Consumer<EventSyntax<S, E, R>> leave) {
        leave.accept(new EventSyntaxImpl<>(current));
        current = null;
        return (SELF) this;
    }

    @Override
    public Process<S, E, R> build() {
        final FsmDefinition definition = new FsmDefinition(ExecutionOrder.FIRST_TO_LAST);
        nodes.keySet().forEach(ref -> register(ref, definition));
        nodes.entrySet()
                .forEach(e -> {
                    Ref<S> ref = e.getKey();
                    Node<S,E,R,?> node = e.getValue();
                    node.exits
                            .forEach(exit -> {
                                Node<S, E, R, ?> nodeTo = nodes.get(exit.refTo);
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

    static class Started<S, E, R> extends ProcessBuilderImpl<S, E, R, ProcessBuilder.StartedSyntax<S,E,R>>
            implements ProcessBuilder.StartedSyntax<S,E,R> {

        public Started(StateFactory<S> stateFactory) {
            this(stateFactory, new Ref<>("START"), new Ref<>("END"));
            this.current = getNodeByRef(startRef);
        }

        private Started(StateFactory<S> stateFactory, Ref<S> startRef, Ref<S> endRef) {
            super(stateFactory, startRef, endRef, new HashMap<>());
        }

        @Override
        protected Node<S, E, R, ?> doCreateNode(Ref<S> ref, Action<R, Object> action) {
            return new Node.RootNode<>(this, ref, action);
        }

        public BuilderSyntax<S, E, R> end() {
            getNodeByRef(endRef);
            if (this.current != null) {
                this.current.addExit(new Exit<>(endRef, (E) Node.THEN, Guard.allow()));
            }
            return this::build;
        }

    }

    static class Sub<S, E, R> extends ProcessBuilderImpl<S, E, R, ProcessBuilder.SubProcessSyntax<S,E,R>>
            implements ProcessBuilder.SubProcessSyntax<S,E,R> {

        Sub(StateFactory<S> stateFactory, Ref<S> startRef, Ref<S> endRef, Map<Ref<S>, Node<S,E,R,?>> nodes) {
            super(stateFactory, startRef, endRef, nodes);
            this.current = getNodeByRef(startRef);
        }

        @Override
        protected Node<S, E, R, ?> doCreateNode(Ref<S> ref, Action<R, Object> action) {
            return new Node.BranchNode<>(this, ref, action);
        }

        @Override
        public FinishedSyntax jump(Ref<S> ref) {
            ((Node.BranchNode) current).jump(ref);
            return null;
        }

        @Override
        public FinishedSyntax end() {
            ((Node.BranchNode) current).end();
            return null;
        }
    }

}
