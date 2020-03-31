package fsm.process;

import fsm.Guard;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChooseSyntax<S,E,R> {
    public final List<Option> options = new ArrayList<>();

    public ChooseSyntax when(Guard guard, Consumer<ProcessBuilder.StartedSyntax> config) {
        options.add(new Option(guard, config));
        return this;
    }
    public ChooseSyntax otherwise(Consumer<ProcessBuilder.StartedSyntax> config) {
        Guard otherwise = Guard.and(
            options.stream()
                .map(o -> Guard.not(o.guard))
                .collect(Collectors.toList())
        );
        return when(otherwise, config);
    }
    public static ChooseSyntax choose() {
        return new ChooseSyntax();
    }

    @RequiredArgsConstructor
    public static class Option<S,E,R> {
        public final Guard<R, Object> guard;
        public final Consumer<ProcessBuilder.StartedSyntax<S,E,R>> consumer;
    }
}
