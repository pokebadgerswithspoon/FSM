package fsm.process;

import fsm.Guard;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChooseSyntax {
    final List<Option> options = new ArrayList<>();

    ChooseSyntax when(Guard guard, Consumer<Process> config) {
        options.add(new Option(guard, config));
        return this;
    }
    ChooseSyntax otherwise(Consumer<Process> config) {
        return when(Guard.ALLOW, config);
    }
    static ChooseSyntax choose() {
        return new ChooseSyntax();
    }

    @RequiredArgsConstructor
    static class Option {
        final Guard guard;
        final Consumer<Process> consumer;
    }
}
