package fsm.process.impl;

import fsm.Action;
import fsm.process.Process;
import fsm.process.Ref;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static fsm.Action.TAKE_NO_ACTION;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public class Branch<S> extends Node<S> {

    public final Ref<S> endRef;
    private Map<Ref<S>, Node<S>> siblings = new HashMap<>();

    public Branch(Ref<S> endRef) {
        this(endRef, TAKE_NO_ACTION);
    }
    public Branch(Ref<S> endRef, Action onEnter) {
        super(null, new Ref<>());
        this.endRef = ofNullable(endRef).orElseGet(() -> new Ref());
        this.branch = this;
    }

    Node<S> add(Ref<S> ref, Consumer<Node<S>> brunch) {
        requireNonNull(ref);
        if(siblings.containsKey(ref)) {
            throw new IllegalArgumentException("Reference is alraedy known");
        }
        Node<S> node = new Node<>(this, ref, TAKE_NO_ACTION);
        brunch.accept(node);
        siblings.put(ref, node);
        return this;
    }

    Process build() {
        throw new IllegalStateException("Not implemented yet");
    }

}
