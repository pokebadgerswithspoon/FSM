package fsm.process.impl;

import fsm.Action;
import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.ProcessBuilder;
import fsm.process.Ref;
import fsm.process.StayUntil;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static fsm.Action.TAKE_NO_ACTION;
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
        return addExit(action, ref, null);
    }

    Node<S> addExit(Action action, Ref<S> refTo, Guard guard) {
        Node<S> node = processBuilder.nodes.computeIfAbsent(refTo, (r) -> new Node<>(processBuilder, r, TAKE_NO_ACTION));
        exits.add(new Exit<>(refTo, THEN, guard));
        return node;
    }

    public Branch<S> choose(ChooseSyntax chooseSyntax) {
        for(ChooseSyntax.Option option: chooseSyntax.options) {
            Action action = TAKE_NO_ACTION;
            ProcessBuilderImpl<S> b = processBuilder.createSubProcessBuilder(new Ref<>("WHEN"));
            option.consumer.accept(b);
            addExit(action, b.startRef, option.guard);
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
        Collection<StayUntil.Exit> entries = stayUntil.exits.values();
        for(StayUntil.Exit exit: entries) {
            if(exit.refTo != null) {
                addExit(TAKE_NO_ACTION, exit.refTo, exit.guard);
            } else if(exit.builder != null) {
                ProcessBuilderImpl<S> b = processBuilder.createSubProcessBuilder(new Ref<>("UNTIL"));
                exit.builder.accept(b);
                addExit(TAKE_NO_ACTION, b.startRef, exit.guard);
            } else {
                requireNonNull(exit.refTo, "refTo (or subprocess) from StayUntil.Exit should should be non null");
            }
        }
        return newBranch();
    }
}
