package fsm.process.impl;

import fsm.Action;
import fsm.FsmDefinition;
import fsm.process.Ref;
import fsm.syntax.EventSyntax;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fsm.Action.TAKE_NO_ACTION;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
class Node<S> {


    void then(Ref<S> ref) {
        exit = (leaf, in) -> in.on("then").transition(onEnter).to(ref);
    }



    Exit<S> exit;

    public Node(Ref<S> ref) {
        this(ref, TAKE_NO_ACTION);
    }

    void registerIn(FsmDefinition fsmDefinition) {
        requireNonNull(ref.getState());
        
        for(Node<S> leaf: leafs) {
            exit.register(leaf, fsmDefinition.in(ref.getState()));
        }
                
    }

    final Ref<S> ref;
    final Action onEnter;

    List<Node<S>> leafs = new ArrayList<>();
    
    interface Exit<S> {

        void register(Node<S> leaf, EventSyntax in);
    }
}
