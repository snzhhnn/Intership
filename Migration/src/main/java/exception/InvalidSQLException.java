package exception;


import java.sql.SQLException;

public class InvalidSQLException extends SQLException {
    private static final String message = "invalid SQL statement";

    public InvalidSQLException() {
        super(message);
    }
}