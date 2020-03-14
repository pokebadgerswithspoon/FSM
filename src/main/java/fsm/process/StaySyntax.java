package fsm.process;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StaySyntax {
    public Map<Object, Process.Ref> exits = new HashMap<>();
    public Map<Object, Consumer<Process>> bldr = new HashMap<>();

    public StaySyntax on(Object event, Process.Ref to) {
        exits.put(event, to);
        return this;
    }

    public StaySyntax on(Object event, Consumer<Process> builder) {
        bldr.put(event, builder);
        return this;
    }

    public static StaySyntax exit() {
        return new StaySyntax();
    }

}