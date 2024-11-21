package exception;

public class InvalidClassNameException extends RuntimeException {
    private static final String message = "Invalid Java-based migration class name";

    public InvalidClassNameException() {
        super(message);
    }
}