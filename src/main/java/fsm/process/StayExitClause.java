package fsm.process;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StayExitClause {
    public Map<Object, Process.Ref> exits = new HashMap<>();
    public Map<Object, Consumer<Process>> bldr = new HashMap<>();

    public StayExitClause on(Object event, Process.Ref to) {
        exits.put(event, to);
        return this;
    }

    public StayExitClause on(Object event, Consumer<Process> builder) {
        bldr.put(event, builder);
        return this;
    }


    public static StayExitClause exit() {
        return new StayExitClause();
    }
//    public Collection<ExitSyntax> exits = new ArrayList<>();
//
//    interface ExitSyntax<E> {
//        void onEvent(E event, Process.Ref whereTo);
//    }
}
