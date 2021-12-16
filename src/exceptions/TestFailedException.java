package exceptions;

public class TestFailedException extends RuntimeException {
    public TestFailedException(String msg) {
        super(msg);
    }
}
