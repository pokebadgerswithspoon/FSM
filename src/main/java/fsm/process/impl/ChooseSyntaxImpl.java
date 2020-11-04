package fsm.process.impl;

import fsm.Guard;
import fsm.process.ProcessBuilder;
import fsm.process.ProcessBuilder.ChooseSyntax;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class ChooseSyntaxImpl<S,E,R> implements ChooseSyntax<S,E,R>, ChooseSyntax.End {
    final Node<S,E,R,?> node;
    final List<Exit<S,E,R>> exits = new LinkedList<>();

     @Override
    public ChooseSyntax<S, E, R> when(Guard<R, Object> guard, ProcessBuilder.SubProcess<S, E, R> config) {
        Ref refTo = new Ref();
        config.apply(node.processBuilder.createSubProcessBuilder(refTo));
        exits.add(new Exit<>(refTo, Node.THEN, guard));
        return end();
    }

    @Override
    public End otherwise(ProcessBuilder.SubProcess<S, E, R> config) {
        Ref refTo = new Ref();
        config.apply(node.processBuilder.createSubProcessBuilder(refTo));
        exits.add(new Exit<>(refTo, Node.THEN, otherwiseGuard()));
        return end();
    }

    private ChooseSyntaxImpl<S,E,R> end() {
        exits.forEach(node::addExit);
        return this;
    }


    @Override
    public End otherwise(Ref<S> refTo) {
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
