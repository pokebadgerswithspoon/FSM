package fsm.process.impl;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.Process;
import fsm.process.Ref;
import fsm.process.ProcessBuilder;
import fsm.process.StateFactory;
import fsm.process.StayUntil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class ProcessBuilderImpl<S> implements ProcessBuilder<S>, ProcessBuilder.ProceedSyntax<S>, ProcessBuilder.StartedSyntax<S> {

    private S currentState = null;
    final FsmDefinition definition;
    private Guard guard = null;
    private List<Runnable> onEnd = new ArrayList<>();

    private final StateFactory<S> stateFactory;
    private final Ref<S> endRef = new Ref<>("END");
    private final Ref<S> startRef = new Ref<>("START");

    private static final Action GO = Action.TAKE_NO_ACTION;

    public ProcessBuilderImpl(StateFactory<S> stateFactory) {
        this(new FsmDefinition(), stateFactory);
    }

    private ProcessBuilderImpl(FsmDefinition definition, StateFactory<S> stateFactory) {
        this.stateFactory = stateFactory;
        this.definition = definition;
        register(startRef);
        this.currentState = startRef.getState();
    }

    private ProcessBuilderImpl on(Object event, Action action, Ref<S> ref) {
        requireNonNull(event);
        requireNonNull(action);
        requireNonNull(ref);
        requireRegistered(ref);

        definition.in(currentState)
            .on(event)
            .onlyIf(this.guard)
            .transition(action)
            .to(ref.getState());

        currentState = ref.getState();
        return this;
    }

    private void requireRegistered(Ref ref) {
        if(!ref.isAssigned()) {
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
        return register(ref).on("then", action, ref);
    }

    @Override
    public ProcessBuilder.ProceedSyntax stay(StayUntil exitClause) {
        final S s = this.currentState;

        for(Map.Entry<Object, Consumer<ProcessBuilder.StartedSyntax>> entry: exitClause.bldr.entrySet()) {
            final Object event = entry.getKey();
            final Consumer<ProcessBuilder.StartedSyntax> processConsumer = entry.getValue();

            onEnd.add(() -> {
                S state = this.currentState;
                this.currentState = s;
                Ref<S> next = new Ref("STAY");
                processConsumer.accept(this.register(next).on(event, GO, next));
                this.currentState = state;
            });
        }
        for(Map.Entry<Object, Ref> entry: exitClause.exits.entrySet()) {
            final Object event = entry.getKey();
            final Ref<S> whereTo = entry.getValue();

            onEnd.add(() -> {
                S state = this.currentState;
                this.currentState = s;
                // shall we die if whereTo is unknown?
                on(event, GO, whereTo);
                this.currentState = state;
            });
        }

        return this;
    }

    private ProcessBuilderImpl register(final Ref<S> ref) {
        requireNonNull(ref);
        if(!ref.isAssigned()) {
            S state = stateFactory.createState();
            ref.setState(state);
            definition.registerState(ref.getState());
        }
        return this;
    }


    @Override
    public ProcessBuilder.StartedSyntax<S> add(final Ref<S> ref) {
        requireNonNull(ref);
        register(ref);
        this.currentState = ref.getState();
        return this;
    }


    private void thenEnd() {
        then(Action.TAKE_NO_ACTION, endRef);
    }

    private boolean onEndRunning = false;

    @Override
    public Process end() {
        thenEnd();
        if(onEndRunning) {
            return null;
        }
        onEndRunning = true;
        onEnd.forEach(Runnable::run);
        onEnd.clear();
        onEndRunning = false;
        return new ProcessImpl(
            startRef.getState(),
            endRef.getState(),
            definition
        );
    }

    @Override
    public void go(Ref ref) {
        on("then", Action.TAKE_NO_ACTION, ref);
    }

    @Override
    public ProceedSyntax<S> choose(ChooseSyntax chooseSyntax) {
        final S s = this.currentState;
        for(ChooseSyntax.Option option: chooseSyntax.options) {
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


}
