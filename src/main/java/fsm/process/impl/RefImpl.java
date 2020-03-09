package fsm.process.impl;

import fsm.process.Process;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
class RefImpl implements Process.Ref<Integer> {

    static final Supplier<IllegalArgumentException> ILLEGAL_REF = () -> new IllegalArgumentException("Ref is either foregn or not assigened a state yet");
    int state;
    @Getter(AccessLevel.PACKAGE)
    boolean assigned = false;
    @Getter
    private final String name;

    RefImpl() {
        this("NONE");
    }

    @Override
    public Integer getState() {
        return state;
    }

    public static int stateOf(Process.Ref ref) {
        return Optional.of(ref)
                .filter(RefImpl.class::isInstance)
                .map(RefImpl.class::cast)
                .filter(RefImpl::isAssigned)
                .map(RefImpl::getState)
                .orElseThrow(ILLEGAL_REF);
    }

    static RefImpl processRef(Process.Ref ref) {
        return Optional.of(ref)
                .filter(RefImpl.class::isInstance)
                .map(RefImpl.class::cast)
                .orElseThrow(ILLEGAL_REF);
    }
    static Optional<RefImpl> assignedRef(Process.Ref ref) {
        return Optional.of(ref)
                .filter(RefImpl.class::isInstance)
                .map(RefImpl.class::cast)
                .filter(RefImpl::isAssigned);
    }
}
