package fsm.process;

import fsm.Guard;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static fsm.Guard.ALLOW;

public class StayUntil<S, E> {
    public final Map<E, Exit> exits = new HashMap<>();

    public StayUntil on(E event, Ref<S> to) {
        return on(event, ALLOW, to);
    }
    public StayUntil on(E event, Guard guard, Ref<S> to) {
        exits.put(event, new Exit(to, guard, null));
        return this;
    }

    public StayUntil on(E event, Consumer<ProcessBuilder.StartedSyntax> builder) {
        return on(event, ALLOW, builder);
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