package fsm.process;

import fsm.Action;
import fsm.process.impl.IntRefFactoryImpl;
import fsm.process.impl.ProcessBuilderInitImpl;

public interface ProcessBuilder<S> {

    static <Integer> StartSyntax builder() {
        RefFactory<Integer> refFactory = (RefFactory<Integer>) new IntRefFactoryImpl();
        return new ProcessBuilderInitImpl<Integer>()
            .setRefFactory(refFactory);
    }
    static <S> InitSyntax builder(Class<S> stateClass) {
        return new ProcessBuilderInitImpl<S>();
    }

    interface StartSyntax<S> {
        StartedSyntax<S> start();
    }

    interface InitSyntax<S> {
        StartSyntax<S> setRefFactory(RefFactory<S> refFactory);
    }

    interface StartedSyntax<S> {
        StartedSyntax<S> then(Action action);

        StartedSyntax<S> then(Action action, Process.Ref<S> ref);

        ProceedSyntax<S> choose(ChooseSyntax chooseSyntax);

        ProceedSyntax<S> stay(StayUntil exit);

        Process end();

        void go(Process.Ref<S> ref);
    }

    interface ProceedSyntax<S> {
        ProcessBuilder.StartedSyntax add(Process.Ref<S> ref);

    }

    interface RefFactory<S> {
        Process.Ref<S> create();
        Process.Ref<S> create(String name);

        boolean hasState(Process.Ref<S> ref);

        void assignState(Process.Ref<S> ref);
    }

}
