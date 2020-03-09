package fsm.process.impl;

import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.Process;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class ChooseSyntaxImpl implements ChooseSyntax, ChooseSyntax.WhenSyntax {
    final List<Option> options = new ArrayList<>();

    @Override
    public ChooseSyntaxImpl when(Guard guard, Consumer<Process> config) {
        options.add(new Option(guard, config));
        return this;
    }

    @Override
    public ChooseSyntaxImpl otherwise(Consumer<Process> config) {
        return when(Guard.ALLOW, config);
    }
    static WhenSyntax choose() {
        return new ChooseSyntaxImpl();
    }

    @RequiredArgsConstructor
    static class Option {
        final Guard guard;
        final Consumer<Process> consumer;
    }
}
