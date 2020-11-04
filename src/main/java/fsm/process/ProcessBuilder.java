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

    interface JumpSyntax<S> {

        FinishedSyntax jump(Ref<S> ref);
    }

    interface FinishedSyntax {

    }

    interface InProcessSyntax<S,E,R, SELF extends InProcessSyntax<S,E,R,SELF>> {

        SELF label(Ref<S> ref);

        SELF then(Action<R, Object> action);

        SELF then(Ref<S> ref, Action<R, Object> action);

        SELF stay(Consumer<EventSyntax<S,E,R>> leave);

        SELF thenStay(Action<R, Object> action, Consumer<EventSyntax<S,E,R>> leave);

//        StartedSyntax<S,E,R> thenStay(Ref<S> ref, Action<R, Object> action, Consumer<EventSyntax<S,E,R>> leave);

        SELF choose(Function<ChooseSyntax<S,E,R>, ChooseSyntax.End> choose);
    }

    interface StartedSyntax<S,E,R> extends InProcessSyntax<S,E,R, StartedSyntax<S,E,R>> {
        BuilderSyntax<S,E,R> end();
    }
    interface SubProcessSyntax<S,E,R> extends InProcessSyntax<S,E,R, SubProcessSyntax<S,E,R>>, JumpSyntax<S> {
        FinishedSyntax end();
    }

    interface EndSyntax<S, E, R> {
        BuilderSyntax end();
    }

    interface EventSyntax<S,E,R> {

        default EventSyntax<S,E,R> on(E event, Ref<S> refTo) {
            return this.on(event, null, refTo);
        }
        EventSyntax<S, E, R> on(E event, Guard<R, Object> guard, Ref<S> refTo);

        default EventSyntax<S,E,R> on(E event, SubProcess<S,E,R> process) {
            return this.on(event, null, process);
        }
        EventSyntax<S, E, R> on(E event, Guard<R, Object> guard, SubProcess process);

    }


    interface SubProcess<S,E,R> extends Function<SubProcessSyntax<S,E,R>, FinishedSyntax> {

    }

    interface ChooseSyntax<S, E, R> {
        ChooseSyntax<S, E, R> when(Guard<R, Object> guard, SubProcess<S,E,R> config);

        ChooseSyntax.End otherwise(SubProcess<S,E,R> config);

        ChooseSyntax.End otherwise(Ref<S> refTo);

        interface End {

        }
    }

    interface BuilderSyntax<S,E,R> extends FinishedSyntax {
        Process<S,E,R> build();
    }

    Process<S,E,R> build();
}
