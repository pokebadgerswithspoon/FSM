package fsm.process;

import fsm.Action;
import fsm.FsmDefinition;

import java.util.function.Consumer;

public interface Process {

    Process then(Action action);

    Process then(Action action, Ref ref);

    Process stay(Consumer<StaySyntax> staySyntax);

    Process add(final Ref ref);

    FsmDefinition end();

    void go(Ref ref);

    Process choose(Consumer<ChooseSyntax> chooseSyntax);

    interface Ref<S> {
        String getName();

        S getState();
    }

}
