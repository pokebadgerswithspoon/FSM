package fsm.process;

public interface StateFactory<S> {
    S createState();
}
