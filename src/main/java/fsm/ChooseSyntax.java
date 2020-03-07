package fsm;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ChooseSyntax {
    final List<Option> options = new ArrayList<>();

    ChooseSyntax when(Guard guard, Consumer<Builder> config) {
        options.add(new Option(guard, config));
        return this;
    }
    ChooseSyntax otherwise(Consumer<Builder> config) {
        return when(Guard.ALLOW, config);
    }
    static ChooseSyntax choose() {
        return new ChooseSyntax();
    }

    @RequiredArgsConstructor
    static class Option {
        final Guard guard;
        final Consumer<Builder> consumer;
    }
}
