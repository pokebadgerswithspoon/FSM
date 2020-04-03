package fsm.process;

import org.junit.Test;

import java.util.UUID;

public class ProcessBuilderTest {

    @Test
    public void ensureGenericsPassed() {
        Process<UUID, String, Rt> process = ProcessBuilder.builder(new UuidStateFactory(), String.class, Rt.class)
                .start()
                .then((runtime, p) -> {
                })
                .end()
                .build();

        process.getFsmDefinition();


    }

    private class UuidStateFactory implements StateFactory<UUID>{

        @Override
        public UUID createState(Ref<UUID> ref) {
            return UUID.randomUUID();
        }
    }

    private class Rt {
    }
}