package fsm.process.impl;

import com.sun.org.apache.xpath.internal.axes.ChildIterator;
import fsm.Action;
import fsm.Guard;
import fsm.process.ChooseSyntax;
import fsm.process.Ref;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

import static fsm.Action.TAKE_NO_ACTION;

class Node<S> {

    private static final Object THEN = "THEN";
    protected Branch<S> branch;
    final Ref<S> ref;
    final Action onEnter;
    List<Exit<S>> exits = new LinkedList<>();

    Node(Branch<S> branch) {
        this(branch, null);
    }

    Node(Branch<S> branch, Ref<S> ref) {
        this(branch, ref, TAKE_NO_ACTION);
    }
    Node(Branch<S> branch, Ref<S> ref, Action onEnter) {
        this.branch = branch;
        this.ref = ref;
        this.onEnter = onEnter;
    }


    Node<S> then(Action action) {
        return then(action, null);

    }
    Node<S> then(Action action, Ref<S> refTo) {
        if(!exits.isEmpty()) {
            throw new IllegalStateException("Can not apply .then() to this node");
        }
        Node<S> node = new Node<>(branch, refTo, action);
        exits.add(new Exit<>(node, THEN, null));
        return node;
    }

    Branch<S> choose(ChooseSyntax chooseSyntax) {
        for(ChooseSyntax.Option option: chooseSyntax.options) {

            Ref<S> refTo = new Ref<>();
            Action action = TAKE_NO_ACTION;
            Node<S> node = new Node<>(branch, refTo, action);
            option.consumer.accept(node);
            exits.add(new Exit<>(node, THEN, option.guard));
        }
        return  branch;
    }



    void end() {
        then(TAKE_NO_ACTION, branch.endRef);
    }


    @RequiredArgsConstructor
    static class Exit<S> {

        final Node<S> nodeTo;
        final Object event;
        final Guard guard;

    }
}
