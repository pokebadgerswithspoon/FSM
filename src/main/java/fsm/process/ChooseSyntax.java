package fsm.process;

import fsm.Guard;

import java.util.function.Consumer;

public interface ChooseSyntax {
    WhenSyntax when(Guard guard, Consumer<Process> config);

    interface WhenSyntax extends ChooseSyntax {
        ChooseSyntax otherwise(Consumer<Process> config);
    }
}
