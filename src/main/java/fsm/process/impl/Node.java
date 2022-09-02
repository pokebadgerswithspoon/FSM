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
class Node<S,E,R, SELF extends Node<S,E,R, SELF>> {

    static final Object THEN = "THEN";
    ProcessBuilderImpl<S,E,R, ?> processBuilder;
    final Ref<S> ref;
    final Action<R, Object, S> onEnter;
    final List<Exit<S, E, R>> exits = new LinkedList<>();

    Node(ProcessBuilderImpl<S,E,R, ?> processBuilder, Ref<S> ref, Action<R,Object, S> onEnter) {
        this(ref, onEnter);
        this.processBuilder = processBuilder;
    }

    public SELF then(Action<R, Object, S> action) {
        return then(new Ref<>(), action);
    }

    public SELF label(Ref<S> ref) {
        return (SELF) then(ref, Action.NOOP);
    }

    public SELF thenStay(Action<R, Object, S> action, Consumer<ProcessBuilder.EventSyntax<S, E, R>> leave) {
        SELF node = (SELF) processBuilder.createAndRegisterNode(new Ref<>(), action);
        leave.accept(new EventSyntaxImpl<>(node));
        return node;
    }

    public SELF stay(Consumer<ProcessBuilder.EventSyntax<S, E, R>> leave) {
        SELF node = (SELF) processBuilder.createAndRegisterNode(new Ref<>(), Action.NOOP);
        leave.accept(new EventSyntaxImpl<>(node));
        return node;
    }

//    @Override
//    public ProcessBuilder.ProceedSyntax<S, E, R> thenStay(Ref<S> ref, Action<R, Object> action, Consumer<ProcessBuilder.EventSyntax<S, E, R>> leave) {
//        return this.label(ref).thenStay(action, leave);
//    }

    public SELF then(Ref<S> refTo, Action<R, Object, S> action) {
        if(!exits.isEmpty()) {
            throw new IllegalStateException("Can not apply .then() to this node");
        }
        SELF node = (SELF) processBuilder.createAndRegisterNode(refTo, action);
        exits.add(new Exit<S, E, R>(node.ref, (E) THEN, Guard.allow()));
        return node;

    }

    @Deprecated
    public SELF then(Action<R, Object, S> action, Ref<S> refTo) {
        return this.then(refTo, action);
    }

    void addExit(E event, Ref<S> refTo, Guard guard) {
        addExit(new Exit<S, E, R>(refTo, event, guard));
    }
    SELF addExit(Exit<S,E,R> exit) {
        SELF node = (SELF) processBuilder.getNodeByRef(exit.refTo);
        exits.add(exit);
        return node;
    }

    public SELF choose(Function<ProcessBuilder.ChooseSyntax<S, E, R>, ProcessBuilder.ChooseSyntax.End> choose) {
//        SELF node = (SELF) processBuilder.createAndRegisterNode(new Ref<>(), Action.NOOP);
        choose.apply(new ChooseSyntaxImpl<>(this));
        return (SELF) this;
    }

    static class RootNode<S,E,R> extends Node<S,E,R, RootNode<S,E,R>> implements  ProcessBuilder.StartedSyntax<S,E,R>{
        RootNode(ProcessBuilderImpl.Started<S, E, R> processBuilder, Ref<S> ref, Action<R, Object, S> action) {
            super(processBuilder, ref, action);
        }
        @Override
        public ProcessBuilder.BuilderSyntax<S, E, R> end() {
            return  ((ProcessBuilderImpl.Started<S,E,R>) processBuilder).end();
        }
    }

    static class BranchNode<S,E,R> extends Node<S,E,R, BranchNode<S,E,R>> implements ProcessBuilder.SubProcessSyntax<S,E,R>{
        BranchNode(ProcessBuilderImpl<S,E,R, ?> processBuilder, Ref<S> ref, Action<R,Object, S> onEnter) {
            super(processBuilder, ref, onEnter);
        }

        @Override
        public ProcessBuilder.BuilderSyntax<S, E, R> end() {
            jump(processBuilder.endRef);
            return null;
//        return processBuilder.end();
        }
        @Override
        public ProcessBuilder.FinishedSyntax jump(Ref<S> ref) {
            exits.add(new Exit<>(ref, (E) THEN, null));
            return null;
        }
    }


}
