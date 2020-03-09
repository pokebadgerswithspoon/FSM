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

@RequiredArgsConstructor
public class Process {

    static final Object ENTER = "ENTER";
    private static final Object TIMEOUT = "TIMEOUT";
    private Object entered = ENTER;
    private int currentState = 0;
    private int lastKnownState = 0;
    private final FsmDefinition definition;
    private Guard guard = null;

    private List<Runnable> onEnd = new ArrayList<>();
    private final Ref endRef = new Ref("END");
    private final Ref startRef = new Ref("START");

    private static final Action GO = Action.TAKE_NO_ACTION;

    static Process start() {

        Process process = new Process(new FsmDefinition());
        process.startRef.assigned = true;
        process.startRef.state = process.currentState;
        return process;
    }

    private Process on(Object event, Action action, Ref ref) {
        requireNonNull(event);
        requireNonNull(action);
        requireNonNull(ref);
        requireRegistered(ref);
        definition.in(currentState).on(event).transition(action).to(ref.state);
        currentState = ref.state;
        return this;
    }

    private void requireRegistered(Ref ref) {
        if(!ref.assigned) {
            throw new IllegalStateException(format("Ref %s must be known to process, consider .add()", ref));
        }
    }


    Process then(Action action) {
        requireNonNull(action);
        return then(action, new Ref());
    }

    Process then(Action action, Ref ref) {
        return add(ref)
                .on("then", action, ref);
    }

    Process stay(StayExitClause exitClause) {
        final int s = this.currentState;

        for(Map.Entry<Object, Consumer<Process>> entry: exitClause.bldr.entrySet()) {
            final Object event = entry.getKey();
            final Consumer<Process> processConsumer = entry.getValue();
            onEnd.add(() -> {
                processConsumer.accept(this);
            });
        }
        for(Map.Entry<Object, Ref> entry: exitClause.exits.entrySet()) {
            final Object event = entry.getKey();
            final Ref whereTo = entry.getValue();

            onEnd.add(() -> {
                // shall we die if whereTo is unknown?
                on(event, GO, whereTo);
            });
        }

        return this;
    }

    public Process add(final Ref ref) {
        requireNonNull(ref);
        if(!ref.assigned) {
            ref.state = ++lastKnownState;
            ref.assigned = true;
            definition.registerState(ref.state);
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

    public Process choose(ChooseSyntax chooseSyntax) {
        final int s = this.currentState;
        for(ChooseSyntax.Option option: chooseSyntax.options) {
            onEnd.add(() -> {
                int prevS = this.currentState;
                Object prevE = this.entered;
                this.currentState = s;
                this.guard = option.guard;
                option.consumer.accept(this);
                this.guard = null;
                this.currentState = prevS;
                this.entered = prevE;
            });
        }
        return this;
    }



    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    @ToString
    public static class Ref {
        private int state;
        private boolean assigned = false;
        private final String name;

        public Ref() {
            this("NONE");
        }
    }
}
