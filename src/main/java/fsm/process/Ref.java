package fsm.process;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
@Getter
@Setter(AccessLevel.PACKAGE)
public class Ref<S> {
    private S state;
    private final String comment;
    private boolean assigned;

    public Ref() {
        this(null);
    }

    public void setState(S state) {
        this.state = state;
        this.assigned = true;
    }
}
