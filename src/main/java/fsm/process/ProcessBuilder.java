package fsm.process;

import fsm.Action;
import fsm.process.impl.IntStateFactory;
import fsm.process.impl.ProcessBuilderInitImpl;

import java.util.Map;
import java.util.function.Consumer;

public interface ProcessBuilder<S,E,R> {

    static StartSyntax<Integer, String, Map> builder() {
        return builder(String.class, Map.class);
    }
    static <E,R> StartSyntax<Integer, E, R> builder(Class<E> event, Class<R> runtime) {
        StateFactory<Integer> stateFactory = new IntStateFactory();
        return new ProcessBuilderInitImpl()
            .setRefFactory(stateFactory);
    }
    static <S,E,R> StartSyntax builder(StateFactory<S> refFactory,Class<E> event, Class<R> runtime) {
        return new ProcessBuilderInitImpl<S,E,R>()
            .setRefFactory(refFactory);
    }

    interface StartSyntax<S,E,R> {
        StartedSyntax<S, E, R> start();
    }

    interface StartedSyntax<S,E,R> extends EndSyntax<S,E,R> {
        StartedSyntax<S,E,R> then(Action<R, Object> action);

        StartedSyntax<S,E,R> then(Action<R, Object> action, Ref<S> ref);

        ProceedSyntax<S,E,R> choose(ChooseSyntax chooseSyntax);

        ProceedSyntax<S,E,R> stay(StayUntil exit);

        void go(Ref<S> ref);
    }

    interface ProceedSyntax<S,E,R> extends EndSyntax<S,E,R> {
        ProcessBuilder.ProceedSyntax<S,E,R> add(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax> process);
    }

    interface EndSyntax<S,E,R> {
        ProcessBuilder<S,E,R> end();
    }

    Process<S,E,R> build();
}
