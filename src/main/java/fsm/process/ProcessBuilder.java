package fsm.process;

import fsm.Action;
import fsm.process.impl.IntStateFactory;
import fsm.process.impl.ProcessBuilderInitImpl;

import java.util.function.Consumer;

public interface ProcessBuilder<S> {

    static <Integer> StartSyntax builder() {
        StateFactory<Integer> stateFactory = (StateFactory<Integer>) new IntStateFactory();
        return new ProcessBuilderInitImpl<Integer>()
            .setRefFactory(stateFactory);
    }
    static <S> StartSyntax builder(StateFactory<S> refFactory) {
        return new ProcessBuilderInitImpl<S>()
            .setRefFactory(refFactory);
    }

    interface StartSyntax<S> {
        StartedSyntax<S> start();
    }

    interface StartedSyntax<S> extends EndSyntax<S> {
        StartedSyntax<S> then(Action action);

        StartedSyntax<S> then(Action action, Ref<S> ref);

        ProceedSyntax<S> choose(ChooseSyntax chooseSyntax);

        ProceedSyntax<S> stay(StayUntil exit);
        void go(Ref<S> ref);
    }


    interface ProceedSyntax<S> extends EndSyntax<S> {
        ProcessBuilder.ProceedSyntax<S> add(Ref<S> ref, Consumer<ProcessBuilder.StartedSyntax> process);

        ProcessBuilder.ProceedSyntax<S> add(Ref<S> ref);
    }

    interface EndSyntax<S> {
        Process end();
    }
}
