package fsm.process;

import java.util.function.Consumer;

public interface StaySyntax<E extends Object> {

    StaySyntax on(E event, Consumer<Process> builder);

    StaySyntax on(E event, Process.Ref to);

}
