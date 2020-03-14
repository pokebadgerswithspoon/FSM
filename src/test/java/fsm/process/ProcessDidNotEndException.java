package fsm.process;

public class ProcessDidNotEndException extends RuntimeException {
    public ProcessDidNotEndException(String message) {
        super(message);
    }
}
