package fsm.process.impl;

import fsm.Action;
import fsm.Guard;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class Node<S,E,R> implements ProcessBuilder.StartedSyntax<S,E,R> {

    static final Object THEN = "THEN";
    ProcessBuilderImpl<S,E,R> processBuilder;
    final Ref<S> ref;
    final Action<R, Object> onEnter;
    final List<Exit<S, E, R>> exits = new LinkedList<>();

    Node(ProcessBuilderImpl<S,E,R> processBuilder, Ref<S> ref, Action<R,Object> onEnter) {
        this(ref, onEnter);
        this.processBuilder = processBuilder;
    }

    @Override
    public Node<S,E,R> then(Action<R, Object> action) {
        return then(action, new Ref<S>());
    }

    @Override
    public Node<S,E,R> then(Action<R, Object> action, Ref<S> refTo) {
        if(!exits.isEmpty()) {
            throw new IllegalStateException("Can not apply .then() to this node");
        }
        Node<S,E,R> node = processBuilder.createNode(refTo, action);
        exits.add(new Exit<S, E, R>(node.ref, (E) THEN, Guard.ALLOW));
        return node;
    }

    void addExit(E event, Ref<S> refTo, Guard guard) {
        addExit(new Exit<S, E, R>(refTo, event, guard));
    }
    Node<S,E,R> addExit(Exit<S,E,R> exit) {
        Node<S,E,R> node = processBuilder.getNodeByRef(exit.refTo);
        exits.add(exit);
        return node;
    }

    @Override
    public ProcessBuilder.ProceedSyntax<S, E, R> choose(Function<ProcessBuilder.ChooseSyntax<S, E, R>, ProcessBuilder.ChooseSyntax.End<S,E,R>> choose) {
        choose.apply(new ChooseSyntaxImpl<S,E,R>(this));
        return newBranch();
    }

    private Branch<S,E,R> newBranch() {
        return new Branch<>(new Ref<>(), new Ref<>(), processBuilder);
    }

    @Override
    public ProcessBuilder<S,E,R> end() {
        return processBuilder.end();
    }

    @Override
    public void go(Ref<S> ref) {
        exits.add(new Exit<S,E,R>(ref, (E) THEN, null));
    }

    @Override
    public ProcessBuilder.ProceedSyntax<S, E, R> waitFor(Consumer<ProcessBuilder.EventExitSyntax<S, E, R>> events) {
        events.accept(new EventExitSyntaxImpl<>(this));
        return newBranch();
    }
}
