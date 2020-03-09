package fsm.process.impl;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.Process;
import fsm.process.StaySyntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static fsm.process.impl.RefImpl.processRef;
import static fsm.process.impl.RefImpl.stateOf;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class ProcessImpl implements Process {

    private int currentState = 0;
    private int lastKnownState = 0;
    private final FsmDefinition definition;
    private Guard guard = null;

    private List<Runnable> onEnd = new ArrayList<>();
    private final Ref endRef = new RefImpl("END");

    private static final Action GO = Action.TAKE_NO_ACTION;

    public static Process start() {
        return start(new RefImpl("START"));
    }

    private static Process start(RefImpl startRef) {
        ProcessImpl process = new ProcessImpl(new FsmDefinition(), startRef);
        return process;
    }

    private ProcessImpl(FsmDefinition definition, RefImpl startRef) {
        startRef.assigned = true;
        startRef.state = currentState;
        this.definition = definition;
    }

    private ProcessImpl on(Object event, Action action, Ref ref) {
        requireNonNull(event);
        requireNonNull(action);
        requireNonNull(ref);
        requireRegistered(ref);
        definition.in(currentState).on(event).transition(action).to(stateOf(ref));
        currentState = stateOf(ref);
        return this;
    }

    private void requireRegistered(Ref ref) {
        if(!Optional.of(ref)
                .filter(RefImpl.class::isInstance)
                .map(RefImpl.class::cast)
                .map(RefImpl::isAssigned)
                .orElse(false)
        ) {
            throw RefImpl.ILLEGAL_REF.get();
        }
    }


    public Process then(Action action) {
        requireNonNull(action);
        return then(action, new RefImpl());
    }

    public Process then(Action action, Ref ref) {
        return add(ref)
                .on("then", action, ref);
    }

    public Process stay(Consumer<StaySyntax> configuration) {
        StaySyntaxImpl exit = new StaySyntaxImpl();
        configuration.accept(exit);
        final int s = this.currentState;

        for(Map.Entry<Object, Consumer<Process>> entry: exit.bldr.entrySet()) {
            final Object event = entry.getKey();
            final Consumer<Process> processConsumer = entry.getValue();

            onEnd.add(() -> {
                int state = this.currentState;
                this.currentState = s;
                Ref r = new RefImpl();
                processConsumer.accept(this.add(r).on(event, Action.TAKE_NO_ACTION, r));
                this.currentState = state;
            });
        }
        for(Map.Entry<Object, Ref> entry: exit.exits.entrySet()) {
            final Object event = entry.getKey();
            final Ref whereTo = entry.getValue();

            onEnd.add(() -> {
                // shall we die if whereTo is unknown?
                on(event, GO, whereTo);
            });
        }

        return this;
    }

    public ProcessImpl add(final Ref ref) {
        requireNonNull(ref);
        RefImpl refImpl = processRef(ref);

        if(!refImpl.assigned) {
            refImpl.state = ++lastKnownState;
            refImpl.assigned = true;
            definition.registerState(refImpl.state);
        }
        return this;
    }


    private void doEnd() {
        add(endRef).then(Action.TAKE_NO_ACTION, endRef);
    }
    private boolean onEndRunning = false;
    public FsmDefinition end() {
        doEnd();
        if(onEndRunning) {
            return null;
        }
        onEndRunning = true;
        onEnd.forEach(Runnable::run);
        onEnd.clear();
        onEndRunning = false;
        return definition;
    }

    public void go(Ref ref) {
        on("then", Action.TAKE_NO_ACTION, ref);
    }

    public ProcessImpl choose(Consumer<ChooseSyntax> config) {

        ChooseSyntaxImpl chooseSyntax = new ChooseSyntaxImpl();
        config.accept(chooseSyntax);

        final int s = this.currentState;
        for(ChooseSyntaxImpl.Option option: chooseSyntax.options) {
            onEnd.add(() -> {
                int prevS = this.currentState;
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
