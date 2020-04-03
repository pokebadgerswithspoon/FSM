package fsm.process;

import fsm.Action;
import fsm.Guard;
import fsm.process.impl.IntStateFactory;
import fsm.process.impl.ProcessBuilderInitImpl;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ProcessBuilder<S,E,R> {

    static StartSyntax<Integer, String, Map> builder() {

        return builder(String.class, Map.class);
    }
    static <E,R> StartSyntax<Integer, E, R> builder(Class<E> event, Class<R> runtime) {
        StateFactory<Integer> stateFactory = new IntStateFactory();
        return new ProcessBuilderInitImpl<Integer, E, R>()
            .setRefFactory(stateFactory);
    }
    static <S,E,R> StartSyntax<S, E, R> builder(StateFactory<S> refFactory,Class<E> event, Class<R> runtime) {
        return new ProcessBuilderInitImpl<S,E,R>()
            .setRefFactory(refFactory);
    }

    interface StartSyntax<S,E,R> {
        StartedSyntax<S, E, R> start();
    }

    interface StartedSyntax<S,E,R> extends EndSyntax<S,E,R> {
        StartedSyntax<S,E,R> then(Action<R, Object> action);

        StartedSyntax<S,E,R> then(Action<R, Object> action, Ref<S> ref);

        ProceedSyntax<S,E,R> choose(Function<ChooseSyntax<S,E,R>, ChooseSyntax.End<S,E,R>> choose);

        ProceedSyntax<S,E,R> stay(Consumer<EventExitSyntax<S,E,R>> leave);

        void go(Ref<S> ref);
    }

    interface ProceedSyntax<S,E,R> extends EndSyntax<S,E,R> {
        ProcessBuilder.ProceedSyntax<S,E,R> add(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> process);
    }

    interface EndSyntax<S,E,R> {
        ProcessBuilder<S,E,R> end();
    }
    interface EventExitSyntax<S,E,R> {

        default EventExitSyntax<S,E,R> on(E event, Ref<S> refTo) {
            return this.on(event, refTo, null);
        }
        EventExitSyntax<S, E, R> on(E event, Ref<S> refTo, Guard<R, Object> guard);

        default EventExitSyntax<S,E,R> on(E event, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> process) {
            Guard<R, Object> ALLOW = (r, p) -> true;
            return this.on(event, ALLOW, process);
        }
        EventExitSyntax<S, E, R> on(E event, Guard<R, Object> guard, Consumer<ProcessBuilder.StartedSyntax<S,E,R>> process);
    }

    interface ChooseSyntax<S, E, R> {
        ChooseSyntax<S, E, R> when(Guard<R, Object> guard, Ref<S> refTo);

        ChooseSyntax<S, E, R> when(Guard<R, Object> guard, Consumer<ProcessBuilder.StartedSyntax<S, E, R>> config);

        ChooseSyntax.End<S,E,R> otherwise(Consumer<ProcessBuilder.StartedSyntax<S, E, R>> config);

        ChooseSyntax.End<S,E,R> otherwise(Ref<S> refTo);

        interface End<S,E,R> {

        }
    }

    Process<S,E,R> build();
}
