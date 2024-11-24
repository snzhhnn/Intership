package migration;

import connection.ConnectionManager;
import exception.InvalidSQLException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class MigrationExecutor {
    private final Connection connection = ConnectionManager.getInstance().getConnection();

    public void execute(String sql) throws IOException, SQLException {
        Statement statement = connection.createStatement();
        try {
            log.info("start execute migration");
            statement.execute(sql);
            log.info("migration completed successfully ");
        } catch (PSQLException e) {
            connection.rollback();
            log.error("all migration was roll back");
            throw new InvalidSQLException();
        }
    }
}
