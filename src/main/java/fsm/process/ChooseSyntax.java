package fsm.process;

import fsm.Guard;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChooseSyntax {
    final List<Option> options = new ArrayList<>();

    ChooseSyntax when(Guard guard, Consumer<Process> config) {
        options.add(new Option(guard, config));
        return this;
    }
    ChooseSyntax otherwise(Consumer<Process> config) {
        Guard otherwise = Guard.and(
                options.stream()
                .map(o -> Guard.not(o.guard))
                .collect(Collectors.toList())
        );
        return when(otherwise, config);
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
