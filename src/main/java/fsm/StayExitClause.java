package fsm;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StayExitClause {
    public List<Clause> list = new ArrayList<>();

    public StayExitClause on(Object event, Consumer<Builder> b) {
        list.add(new Clause(event, b));
        return this;
    }    
    
    public static StayExitClause exit() {
        return new StayExitClause();
    }

    @RequiredArgsConstructor
    public class Clause {
        public final Object event;
        public Action action;
        public Builder.Ref ref;
        final  Consumer<Builder> b;
    }
}
