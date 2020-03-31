package fsm.process;

import fsm.Guard;
import fsm.process.impl.Node;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StayUntil<S, E> {
    public final Map<E, Exit> exits = new HashMap<>();

    public StayUntil on(E event, Guard guard, Ref<S> to) {
        exits.put(event, new Exit(to, guard, null));
        return this;
    }

    public StayUntil on(E event, Guard guard, Consumer<ProcessBuilder.StartedSyntax> builder) {
        exits.put(event, new Exit(null, guard, builder));
        return this;
    }

    public static StayUntil stayUntil() {
        return new StayUntil();
    }

    @RequiredArgsConstructor
    public class Exit {
        public final Ref<S> refTo;
        public final Guard guard;
        public final Consumer<ProcessBuilder.StartedSyntax> builder;
    }

}