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
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static fsm.Action.TAKE_NO_ACTION;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class ProcessBuilderImpl<S> implements ProcessBuilder<S>, ProcessBuilder.ProceedSyntax<S>, ProcessBuilder.StartedSyntax<S> {

    private S currentState = null;
    private Guard guard = null;
    private List<Runnable> onEnd = new ArrayList<>();

    private final StateFactory<S> stateFactory;
    private final Ref<S> endRef = new Ref<>("END");
    private final Ref<S> startRef = new Ref<>("START");

    private final Node root = new Node(startRef);
    private Node current = root;

    private static final Action GO = TAKE_NO_ACTION;

    public ProcessBuilderImpl(StateFactory<S> stateFactory) {
        this.stateFactory = stateFactory;
    }

    private void requireRegistered(Ref ref) {
        if (!ref.isAssigned()) {
            throw new IllegalStateException(format("Ref %s must be known to process, consider .register()", ref));
        }
    }


    @Override
    public ProcessBuilder.StartedSyntax then(Action action) {
        requireNonNull(action);
        return then(action, new Ref<>());
    }

    @Override
    public StartedSyntax then(Action action, Ref ref) {
        Node<S> leaf = new Node<>(ref, action);
        current.then(ref);
        current.leafs.add(leaf);
        this.current = leaf;
        return this;
    }

    public StartedSyntax then(Process subProcess) {
        return then(subProcess, new Ref<>());
    }

    public StartedSyntax then(Process subProcess, Ref ref) {
        requireNonNull(subProcess);
        requireNonNull(ref);

        FsmDefinition subFsmDef = subProcess.getFsmDefinition();

//        return register(ref).on("then", action, ref);
        return null;
    }

    @Override
    public ProcessBuilder.ProceedSyntax stay(StayUntil exitClause) {
        final S s = this.currentState;

        for (Map.Entry<Object, Consumer<ProcessBuilder.StartedSyntax>> entry : exitClause.bldr.entrySet()) {
            final Object event = entry.getKey();
            final Consumer<ProcessBuilder.StartedSyntax> processConsumer = entry.getValue();

            onEnd.add(() -> {
                S state = this.currentState;
                this.currentState = s;
                Ref<S> next = new Ref("STAY");
//                processConsumer.accept(this.register(next).on(event, GO, next));
                this.currentState = state;
            });
        }
        for (Map.Entry<Object, Ref> entry : exitClause.exits.entrySet()) {
            final Object event = entry.getKey();
            final Ref<S> whereTo = entry.getValue();

            onEnd.add(() -> {
                S state = this.currentState;
                this.currentState = s;
                // shall we die if whereTo is unknown?
//                on(event, GO, whereTo);
                this.currentState = state;
            });
        }

        return this;
    }


    @Override
    public ProcessBuilder.ProceedSyntax<S> add(final Ref<S> ref) {
        requireNonNull(ref);
        register(ref);
        this.currentState = ref.getState();
        return this;
    }

    @Override
    public ProcessBuilder.ProceedSyntax<S> add(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax> process) {
        requireNonNull(ref);
        register(ref);
        this.currentState = ref.getState();
        process.accept(this);
        return this;
    }

    private boolean onEndRunning = false;

    @Override
    public void go(Ref ref) {
//        on("then", Action.TAKE_NO_ACTION, ref);
    }

    @Override
    public ProceedSyntax<S> choose(ChooseSyntax chooseSyntax) {
        final S s = this.currentState;
        for (ChooseSyntax.Option option : chooseSyntax.options) {
            onEnd.add(() -> {
                S prevS = this.currentState;
                this.currentState = s;
                this.guard = option.guard;
                option.consumer.accept(this);
                this.guard = null;
                this.currentState = prevS;
            });
        }
        return this;
    }


    @Override
    public ProcessBuilder end() {
        then(TAKE_NO_ACTION, endRef);
        return this;
    }

    @Override
    public Process build() {
        final FsmDefinition definition = new FsmDefinition();

        registerLeaf(root, definition);


        if (onEndRunning) {
            return null;
        }
        onEndRunning = true;
        onEnd.forEach(Runnable::run);
        onEnd.clear();
        onEndRunning = false;
        return null;
//        return new ProcessImpl(
//            startRef.getState(),
//            endRef.getState(),
//            definition
//        );
    }
    private void registerLeaf(Node<S> leaf, FsmDefinition definition) {
        Ref ref = register(leaf.ref, definition);
        leaf.registerIn(definition);

    }


    private Ref register(final Ref<S> ref, FsmDefinition fsmDefinition) {
        requireNonNull(ref);
        if (!ref.isAssigned()) {
            S state = stateFactory.createState();
            ref.setState(state);
            fsmDefinition.registerState(ref.getState());
        }
        return ref;
    }

}
