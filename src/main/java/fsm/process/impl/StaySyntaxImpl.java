package fsm.process.impl;

import fsm.process.Process;
import fsm.process.StaySyntax;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

class StaySyntaxImpl implements StaySyntax<Object> {
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
}
