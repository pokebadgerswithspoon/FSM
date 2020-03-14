package fsm.process;

import fsm.process.Process.Ref;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StayUntil {
    public Map<Object, Ref> exits = new HashMap<>();
    public Map<Object, Consumer<ProcessBuilder.StartedSyntax>> bldr = new HashMap<>();

    public StayUntil on(Object event, Ref to) {
        exits.put(event, to);
        return this;
    }

    public StayUntil on(Object event, Consumer<ProcessBuilder.StartedSyntax> builder) {
        bldr.put(event, builder);
        return this;
    }

    public static StayUntil stayUntil() {
        return new StayUntil();
    }

}