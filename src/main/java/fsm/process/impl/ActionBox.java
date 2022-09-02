package fsm.process.impl;

import fsm.Action;
import fsm.Transition;

import java.util.function.Supplier;

final class ActionBox<R, P, S> implements Action<R, P, S> {
    private Action<R, P, S> boxed = NOOP;
    private boolean taken = false;

    public static <R, P, S> void tryTake(Action<R, P, S> onEnter, Action<R, P, S> action, Supplier<RuntimeException> exceptionSupplier) {
        if(onEnter instanceof ActionBox) {
            ActionBox actionBox = (ActionBox) onEnter;
            actionBox.doTryTake(action, exceptionSupplier);
        } else {
            throw exceptionSupplier.get();
        }
    }

    private void doTryTake(Action<R, P, S> onEnter, Supplier<RuntimeException> exceptionSupplier) {
        if(isTaken()) {
            throw exceptionSupplier.get();
        }
        this.boxed = onEnter;
        this.taken = true;
    }

    private boolean isTaken() {
        return taken;
    }

    @Override
    public void execute(R runtime, Transition<S, P, ?> transition) {
        boxed.execute(runtime, transition);
    }
}
