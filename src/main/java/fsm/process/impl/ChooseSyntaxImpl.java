package fsm.process.impl;

import fsm.Guard;
import fsm.process.ProcessBuilder;
import fsm.process.ProcessBuilder.ChooseSyntax;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class ChooseSyntaxImpl<S,E,R> implements ChooseSyntax<S,E,R>, ChooseSyntax.End<S,E,R> {
    final Node<S,E,R> node;
    final List<Exit<S,E,R>> exits = new LinkedList<>();

    @Override
    public ChooseSyntaxImpl<S, E, R> when(Guard<R, Object> guard, Ref<S> refTo) {
        exits.add(new Exit<>(refTo, (E) Node.THEN, guard));
        return this;
    }
    public ChooseSyntaxImpl<S,E,R> when(Guard<R,Object> guard, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> config) {
        Ref refTo = new Ref();
        config.accept(node.processBuilder.createSubProcessBuilder(refTo));
        exits.add(new Exit<>(refTo, (E) Node.THEN, guard));
        return end();
    }
    public ChooseSyntaxImpl<S,E,R> otherwise(Consumer<ProcessBuilder.StartedSyntax<S,E,R>> config) {
        Guard<R, Object> otherwise = otherwiseGuard();
        Ref refTo = new Ref();
        config.accept(node.processBuilder.createSubProcessBuilder(refTo));
        exits.add(new Exit<>(refTo, (E) Node.THEN, otherwise));
        return end();
    }

    private ChooseSyntaxImpl<S,E,R> end() {
        exits.forEach(node::addExit);
        return this;
    }


    @Override
    public End<S,E,R> otherwise(Ref<S> refTo) {
        Guard<R, Object> guard = otherwiseGuard();
        exits.add(new Exit<>(refTo, (E) Node.THEN, guard));
        return this;
    }

    Guard<R,Object> otherwiseGuard() {
        return Guard.and(this.exits.stream()
                .map(e -> e.guard)
                .map(Guard::not)
                .collect(Collectors.toList()));
    }
}
