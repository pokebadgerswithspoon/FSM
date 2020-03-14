package fsm.process;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.Guard;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class Process {

    private int currentState = -1;
    private int lastKnownState = -1;
    final FsmDefinition definition;
    private Guard guard = null;

    private List<Runnable> onEnd = new ArrayList<>();
    final Ref endRef = new Ref("END");
    final Ref startRef;

    private static final Action GO = Action.TAKE_NO_ACTION;


    public Process() {
        this(new FsmDefinition(), new Ref("Start"));
    }

    private Process(FsmDefinition definition, Ref startRef) {
        this.startRef = startRef;
        this.definition = definition;
    }

    private Process on(Object event, Action action, Ref ref) {
        requireNonNull(event);
        requireNonNull(action);
        requireNonNull(ref);
        requireRegistered(ref);

        definition.in(currentState).on(event).onlyIf(this.guard).transition(action).to(ref.state);

        currentState = ref.state;
        return this;
    }

    private void requireRegistered(Ref ref) {
        if(!ref.assigned) {
            throw new IllegalStateException(format("Ref %s must be known to process, consider .addInternal()", ref));
        }
    }


    Process then(Action action) {
        requireNonNull(action);
        return then(action, new Ref());
    }

    Process then(Action action, Ref ref) {
        return addInternal(ref).on("then", action, ref);
    }

    Process stay(StayExitClause exitClause) {
        final int s = this.currentState;

        for(Map.Entry<Object, Consumer<Process>> entry: exitClause.bldr.entrySet()) {
            final Object event = entry.getKey();
            final Consumer<Process> processConsumer = entry.getValue();

            onEnd.add(() -> {
                int state = this.currentState;
                this.currentState = s;
                Ref r = new Ref("STAY");
                processConsumer.accept(this.addInternal(r).on(event, Action.TAKE_NO_ACTION, r));
                this.currentState = state;
            });
        }
        for(Map.Entry<Object, Ref> entry: exitClause.exits.entrySet()) {
            final Object event = entry.getKey();
            final Ref whereTo = entry.getValue();

            onEnd.add(() -> {
                int state = this.currentState;
                this.currentState = s;
                // shall we die if whereTo is unknown?
                on(event, GO, whereTo);
                this.currentState = state;
            });
        }

        return this;
    }

    private Process addInternal(final Ref ref) {
        requireNonNull(ref);
        if(!ref.assigned) {
            ref.state = ++lastKnownState;
            ref.assigned = true;
            definition.registerState(ref.state);
        }
        return this;
    }
    public Process start() {
        return add(startRef);
    }
    public Process add(final Ref ref) {
        requireNonNull(ref);
        addInternal(ref);
        this.currentState = ref.state;
        return this;
    }


    private void thenEnd() {
        then(Action.TAKE_NO_ACTION, endRef);
    }

    private boolean onEndRunning = false;

    public FsmDefinition end() {
        thenEnd();
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

    public Process choose(ChooseSyntax chooseSyntax) {
        final int s = this.currentState;
        for(ChooseSyntax.Option option: chooseSyntax.options) {
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



    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    @ToString
    public static class Ref {
        public int state;
        private boolean assigned = false;
        private final String name;

        public Ref() {
            this("NONE");
        }
    }
}
