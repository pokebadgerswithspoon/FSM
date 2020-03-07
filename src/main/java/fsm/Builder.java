package fsm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class Builder {
    static final Object ENTER = "ENTER";
    private static final Object TIMEOUT = "TIMEOUT";
    private Object enter = ENTER;
    private int currentState = 0;
    private int lastKnownState = 0;
    private final FsmDefinition definition;
    private Guard guard = null;

    private List<Runnable> onEnd = new ArrayList<>();
    private Ref endRef = new Ref();

    static Builder start() {
        return new Builder(new FsmDefinition());
    }

    Builder then(Action action) {
        return then(action, new Ref());
    }
    Builder then(Action action, Ref ref) {
        requireNonNull(ref);
        if(!ref.assigned) {
            ref.state = ++lastKnownState;
        }
        if(guard == null) {
            definition.in(currentState).on(enter).transition(action).to(ref.state);
        } else {
            definition.in(currentState).on(enter).onlyIf(guard).transition(action).to(ref.state);
        }
        currentState = ref.state;
        return this;
    }


    public Builder add(Action b, Ref ref) {
        requireNonNull(ref);
        if(!ref.assigned) {
            ref.state = ++lastKnownState;
        }
        definition.in(ref.state);
        currentState = ref.state;
        return this;
    }

    Builder stay(StayExitClause exitClause) {
        final int s = this.currentState;
        for(StayExitClause.Clause clause: exitClause.list) {
            onEnd.add(() -> {
                int prevS = this.currentState;
                Object prevE = this.enter;
                this.currentState = s;
                this.enter =  clause.event;
                clause.b.accept(this);
                this.currentState = prevS;
                this.enter = prevE;
            });
        }
        return this;
    }


    private boolean onEndRunning = false;
    public FsmDefinition end() {
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
        definition.in(currentState).on(ENTER).transition().to(ref.state);
    }

    public Builder choose(ChooseSyntax chooseSyntax) {
        final int s = this.currentState;
        for(ChooseSyntax.Option option: chooseSyntax.options) {
            onEnd.add(() -> {
                int prevS = this.currentState;
                Object prevE = this.enter;
                this.currentState = s;
                this.guard = option.guard;
                option.consumer.accept(this);
                this.guard = null;
                this.currentState = prevS;
                this.enter = prevE;
            });
        }
        return this;
    }



    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Ref {
        private int state;
        private boolean assigned = false;
    }
}
