package fsm.process.impl;

import fsm.Action;
import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import fsm.process.StayUntil;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class Node<S> implements ProcessBuilder.StartedSyntax<S> {

    static final Object THEN = "THEN";
    ProcessBuilderImpl<S> processBuilder;
    final Ref<S> ref;
    final Action onEnter;
    final List<Exit<S>> exits = new LinkedList<>();

    Node(ProcessBuilderImpl<S> processBuilder, Ref<S> ref, Action onEnter) {
        this(ref, onEnter);
        this.processBuilder = processBuilder;
    }

    @Override
    public Node<S> then(Action action) {
        return then(action, new Ref<>());
    }

    @Override
    public Node<S> then(Action action, Ref<S> refTo) {
        if(!exits.isEmpty()) {
            throw new IllegalStateException("Can not apply .then() to this node");
        }
        Node<S> node = processBuilder.createNode(refTo, action);
        exits.add(new Exit<S>(node.ref, THEN, Guard.ALLOW));
        return node;
    }

    Node<S> addExit(Object event, Ref<S> refTo, Guard guard) {
        Node<S> node = processBuilder.getNodeByRef(refTo);

        exits.add(new Exit<>(refTo, event, guard));
        return node;
    }

    public Branch<S> choose(ChooseSyntax chooseSyntax) {
        for(ChooseSyntax.Option option: chooseSyntax.options) {
            ProcessBuilderImpl<S> b = processBuilder.createSubProcessBuilder(new Ref<>("WHEN"));
            option.consumer.accept(b);
            addExit(THEN, b.startRef, option.guard);
        }
        return newBranch();
    }
    private Branch<S> newBranch() {
        return new Branch<S>(new Ref<>(), new Ref<>(), processBuilder);
    }

    @Override
    public ProcessBuilder<S> end() {
        return processBuilder.end();
    }

    @Override
    public void go(Ref<S> ref) {
        exits.add(new Exit<>(ref, THEN, null));
    }

    @Override
    public Branch<S> stay(StayUntil stayUntil) {
        Collection<Map.Entry<Object, StayUntil.Exit>> entries = stayUntil.exits.entrySet();
        for(Map.Entry<Object, StayUntil.Exit> entry: entries) {
            Object event = entry.getKey();
            StayUntil.Exit exit = entry.getValue();
            if(exit.refTo != null) {
                addExit(event, exit.refTo, exit.guard);
            } else if(exit.builder != null) {
                ProcessBuilderImpl<S> b = processBuilder.createSubProcessBuilder(new Ref<>("UNTIL"));
                exit.builder.accept(b);
                addExit(event, b.startRef, exit.guard);
            } else {
                requireNonNull(exit.refTo, "refTo (or subprocess) from StayUntil.Exit should should be non null");
            }
        }
        return newBranch();
    }
}
