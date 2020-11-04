package fsm.process.impl;

import fsm.Action;

import java.util.function.Supplier;

final class ActionBox<R, P> implements Action<R, P> {
    private Action<R, P> boxed = TAKE_NO_ACTION;
    private boolean taken = false;

    public static <R, P> void tryTake(Action<R, P> onEnter, Action<R, P> action, Supplier<RuntimeException> exceptionSupplier) {
        if(onEnter instanceof ActionBox) {
            ActionBox actionBox = (ActionBox) onEnter;
            actionBox.doTryTake(action, exceptionSupplier);
        } else {
            throw exceptionSupplier.get();
        }
    }

    private void doTryTake(Action<R, P> onEnter, Supplier<RuntimeException> exceptionSupplier) {
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
    public void execute(R runtime, P payload) {
         boxed.execute(runtime, payload);
    }
}
