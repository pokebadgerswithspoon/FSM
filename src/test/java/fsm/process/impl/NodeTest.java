package fsm.process.impl;

import fsm.Action;
import fsm.Guard;
import fsm.process.Ref;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class NodeTest {

    @Mock
    Ref ref;

    @Mock
    Action action;

    final ProcessBuilderImpl processBuilder = new ProcessBuilderImpl(new IntStateFactory());

    @Test
    public void testThen() {
        Node node = new Node<>(processBuilder, ref, action) ;
        Action a = (r, p) -> {};
        Ref to = new Ref();
        node.then(a, to);
        assertEquals(1, node.exits.size());
    }

    @Test
    public void testAddExit() {
        Node node = new Node<>(processBuilder, ref, action) ;
        Ref to = new Ref();
        Guard guard = (r, p) -> true;
        node.addExit(Node.THEN, to, guard);
        assertEquals(1, node.exits.size());
    }

    @Test
    public void end() {
        Node node = new Node<>(processBuilder, ref, action) ;
        node.end().build();
    }
}
