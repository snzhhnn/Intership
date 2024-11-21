package exception;

import java.sql.SQLException;

public class MigrationExecutedException extends SQLException {
    private static final String message = "Migration was executed";

    public MigrationExecutedException() {
        super(message);
    }
}