package fsm.process.impl;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.Process;
import fsm.process.Process.Ref;
import fsm.process.ProcessBuilder;
import fsm.process.StayUntil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class ProcessBuilderImpl<S> implements ProcessBuilder<S>, ProcessBuilder.ProceedSyntax<S>, ProcessBuilder.StartedSyntax<S> {

    private S currentState = null;
    final FsmDefinition definition;
    private Guard guard = null;
    private List<Runnable> onEnd = new ArrayList<>();

    private final RefFactory<S> refFactory;
    private final Ref<S> endRef ;
    private final Ref<S> startRef;

    private static final Action GO = Action.TAKE_NO_ACTION;

    public ProcessBuilderImpl(RefFactory<S> refFactory) {
        this(new FsmDefinition(), refFactory);
    }

    private ProcessBuilderImpl(FsmDefinition definition, RefFactory<S> refFactory) {
        this.refFactory = refFactory;
        this.definition = definition;
        this.startRef = refFactory.create("START");
        this.endRef = refFactory.create("END");
        register(startRef);
        this.currentState = startRef.state;
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
            .to(ref.state);

        currentState = ref.state;
        return this;
    }

    private void requireRegistered(Ref ref) {
        if(!refFactory.hasState(ref)) {
            throw new IllegalStateException(format("Ref %s must be known to process, consider .register()", ref));
        }
    }


    @Override
    public ProcessBuilder.StartedSyntax then(Action action) {
        requireNonNull(action);
        return then(action, refFactory.create());
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
                Ref<S> next = refFactory.create("STAY");
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
        if(!refFactory.hasState(ref)) {
            refFactory.assignState(ref);
            definition.registerState(ref.state);
        }
        return this;
    }


    @Override
    public ProcessBuilder.StartedSyntax<S> add(final Ref<S> ref) {
        requireNonNull(ref);
        register(ref);
        this.currentState = ref.state;
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
            startRef,
            endRef,
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
